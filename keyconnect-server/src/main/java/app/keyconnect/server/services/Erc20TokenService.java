package app.keyconnect.server.services;

import static app.keyconnect.server.gateways.EthereumGateway.ETH_SCALE;
import static app.keyconnect.server.gateways.EthereumGateway.ROUNDING_MODE;
import static app.keyconnect.server.gateways.EthereumGateway.SCALE;

import app.keyconnect.api.client.model.GenericCurrencyValue;
import app.keyconnect.api.client.model.SubAccountInfo;
import app.keyconnect.server.gateways.EthereumGateway;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.server.gateways.exceptions.UnknownTokenException;
import app.keyconnect.server.gateways.exceptions.UnknownTokenNetworkException;
import app.keyconnect.server.services.networks.NetworkClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;

public class Erc20TokenService {
  public static final String TOKEN_CONFIG_FILE_NAME = "token-config.yaml";
  private static final Logger logger = LoggerFactory.getLogger(Erc20TokenService.class);
  private final TokenConfig tokenConfig;
  private final Set<String> availableNetworks;
  private final CredentialsService<Credentials> ethCredentialsService;
  private final NetworkClientService<Web3j> ethNetworkClientService;

  public Erc20TokenService(
      CredentialsService<Credentials> ethCredentialsService,
      NetworkClientService<Web3j> ethNetworkClientService
  ) {
    this.ethCredentialsService = ethCredentialsService;
    this.ethNetworkClientService = ethNetworkClientService;
    logger.info("Reading token config from {}", TOKEN_CONFIG_FILE_NAME);
    this.tokenConfig = readTokenConfig();
    logger.info("{} tokens read", this.tokenConfig.getTokens().size());
    logger.info("Mapping available networks");
    this.availableNetworks = this.tokenConfig
        .getTokens()
        .values()
        .stream()
        .flatMap(stringStringMap -> stringStringMap.keySet().stream())
        .collect(Collectors.toSet());
    logger.info("{} available networks mapped", this.availableNetworks.size());
  }

  public Set<String> getAvailableNetworks() {
    return new HashSet<>(this.availableNetworks);
  }

  private TokenConfig readTokenConfig() {
    try {
      final InputStream inputStream = new ClassPathResource(TOKEN_CONFIG_FILE_NAME).getInputStream();
      final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      return mapper.readValue(inputStream, TokenConfig.class);
    } catch (IOException e) {
      throw new RuntimeException("Unable to read token-config.yaml from classpath when instantiating Erc20TokenService", e);
    }
  }

  public List<SubAccountInfo> getAllSubAccountInfo(String network, String address) {
    final Map<String, Map<String, String>> tokens = this.tokenConfig
        .getTokens();
    return tokens
        .keySet()
        .stream()
        .map(token -> {
          if (!tokens.get(token).containsKey(network))
            return null;
          return token;
        })
        .filter(Strings::isNotBlank)
        .map(token -> {
          try {
            return getSubAccountInfo(network, address, token);
          } catch (UnknownNetworkException | UnknownTokenException | UnknownTokenNetworkException e) {
            throw new IllegalStateException(e);
          }
        })
        .filter(subAccountInfo -> !new BigDecimal(subAccountInfo.getBalance().getAmount())
            .equals(BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE)))
        .collect(Collectors.toList());
  }

  private SubAccountInfo getSubAccountInfo(String network, String address, String token)
      throws UnknownNetworkException, UnknownTokenException, UnknownTokenNetworkException {
    if (!availableNetworks.contains(network)) throw new UnknownNetworkException(EthereumGateway.CHAIN_ID, network);

    final Map<String, Map<String, String>> tokens = tokenConfig.getTokens();
    if (!tokens.containsKey(token)) throw new UnknownTokenException(EthereumGateway.CHAIN_ID, token);

    final Map<String, String> envContractConfig = tokens.get(token);
    if (!envContractConfig.containsKey(network)) throw new UnknownTokenNetworkException(EthereumGateway.CHAIN_ID, token, network);

    final String contractHash = envContractConfig.get(network);
    final Web3j client = ethNetworkClientService.getClients(network).stream().findFirst().orElseThrow();
    // get credentials from credentials service
    final Credentials credentials = ethCredentialsService.getCredentials();
    // load contract hash
    final ERC20 contract = ERC20.load(contractHash, client, credentials, new DefaultGasProvider());
    final BigInteger balanceNum;
    try {
      balanceNum = contract.balanceOf(address).sendAsync().get();
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException("Unable to get balance, contract=" + contractHash + ", address=" + address, e);
    }
    final BigDecimal tokenBalance = new BigDecimal(balanceNum)
        .divide(ETH_SCALE, SCALE, ROUNDING_MODE);
    // get balance
    return new SubAccountInfo()
        .accountId(contractHash)
        .balance(
            new GenericCurrencyValue()
              .amount(tokenBalance.toString())
              .currency(token.toUpperCase(Locale.getDefault(Category.DISPLAY)))
        );
  }
}
