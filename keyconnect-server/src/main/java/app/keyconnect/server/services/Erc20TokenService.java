package app.keyconnect.server.services;

import static app.keyconnect.server.gateways.EthereumGateway.ETH_SCALE;
import static app.keyconnect.server.gateways.EthereumGateway.ROUNDING_MODE;
import static app.keyconnect.server.gateways.EthereumGateway.SCALE;

import app.keyconnect.api.client.model.GenericCurrencyValue;
import app.keyconnect.api.client.model.SubAccountInfo;
import app.keyconnect.server.services.networks.NetworkClient;
import app.keyconnect.server.services.networks.NetworkClientService;
import app.keyconnect.server.utils.EtherscanUtil;
import app.keyconnect.server.utils.models.EtherscanAccountTransaction;
import app.keyconnect.server.utils.models.SuccessEtherscanResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.tx.gas.DefaultGasProvider;

public class Erc20TokenService {

  public static final String TOKEN_CONFIG_FILE_NAME = "token-config.yaml";
  private static final Logger logger = LoggerFactory.getLogger(Erc20TokenService.class);
  private final TokenConfig tokenConfig;
  private final Set<String> availableNetworks;
  private final CredentialsService<Credentials> ethCredentialsService;
  private final NetworkClientService<Web3j> ethNetworkClientService;
  private final EtherscanUtil etherscanUtil;

  public Erc20TokenService(
      CredentialsService<Credentials> ethCredentialsService,
      NetworkClientService<Web3j> ethNetworkClientService,
      EtherscanUtil etherscanUtil
  ) {
    this.ethCredentialsService = ethCredentialsService;
    this.ethNetworkClientService = ethNetworkClientService;
    this.etherscanUtil = etherscanUtil;
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
      final InputStream inputStream = new ClassPathResource(TOKEN_CONFIG_FILE_NAME)
          .getInputStream();
      final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      return mapper.readValue(inputStream, TokenConfig.class);
    } catch (IOException e) {
      throw new RuntimeException(
          "Unable to read token-config.yaml from classpath when instantiating Erc20TokenService",
          e);
    }
  }

  public List<SubAccountInfo> getAllSubAccountInfo(String network, String address,
      BigInteger latestBlock) {
    int pageNumber = 1;
    final String pageSize = "1000";

    SuccessEtherscanResponse response = etherscanUtil
        .getTokenTransactionsForAccount(network, address, latestBlock.toString(),
            String.valueOf(pageNumber),
            pageSize);
    if (null == response || null == response.getResult() || 0 == response.getResult().length) {
      return Collections.emptyList();
    }

    final List<EtherscanAccountTransaction> transactions = Arrays.stream(
        response.getResult()).collect(Collectors.toList());
    if (transactions.size() == 0) {
      return new ArrayList<>(0);
    }
    List<EtherscanAccountTransaction> pageTx = transactions;
    while (pageTx.size() > 0) {
      response = etherscanUtil
          .getTokenTransactionsForAccount(network, address, latestBlock.toString(),
              String.valueOf(++pageNumber),
              pageSize);
      if (response == null || response.getResult() == null || response.getResult().length == 0) {
        break;  // we've reached end of page
      }
      pageTx = Arrays.stream(response.getResult()).collect(Collectors.toList());
      transactions.addAll(pageTx);
    }
    final Set<Erc20Token> contractsOnAccount = transactions
        .stream()
        .map(t -> new Erc20Token(t.getContractAddress(), t.getTokenSymbol(), t.getTokenDecimal()))
        .collect(Collectors.toSet()); // Set makes it distinct by default
    logger.info("{} contracts found over {} transactions for account {}", contractsOnAccount.size(),
        transactions.size(), address);

    return contractsOnAccount.stream()
        .map(c -> getSubAccountInfo(network, address, c))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    // by tokens
    /*final Map<String, Map<String, String>> tokens = this.tokenConfig
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
        .collect(Collectors.toList());*/
  }

  private SubAccountInfo getSubAccountInfo(String network, String address, Erc20Token token) {
    final NetworkClient<Web3j> networkClient = ethNetworkClientService.getAllMatching(network).stream().findFirst()
        .orElseThrow();
    final Web3j client = networkClient.getClient();
    // get credentials from credentials service
    final Credentials credentials = ethCredentialsService.getCredentials();
    // load contract hash
    final ERC20 contract = ERC20
        .load(token.getContractAddress(), client, credentials, new DefaultGasProvider());
    final String tokenSymbol = token.getTokenSymbol();
    final BigInteger balanceNum;
    try {
      balanceNum = contract.balanceOf(address).sendAsync().get();
    } catch (InterruptedException | ExecutionException e) {
      if (e.getCause() instanceof ContractCallException && e.getCause().getMessage()
          .equals("Empty value (0x) returned from contract")) {
        logger.warn("Contract returned null balance, token={}, address={}", token, address);
        // we skip these contracts, eg 0x4f4a591dfa6bb5ca83f996195654cf7fddd43433 that called self-destruct and are no longer queryable
        return null;
      } else {
        throw new IllegalStateException(
            "Unable to get balance, token=" + token + ", address=" + address, e);
      }
    }
    final BigDecimal tokenBalance = new BigDecimal(balanceNum)
        .divide(ETH_SCALE, SCALE, ROUNDING_MODE);
    // get balance
    return new SubAccountInfo()
        .accountId(token.getContractAddress())
        .balance(
            new GenericCurrencyValue()
                .amount(tokenBalance.toString())
                .currency(tokenSymbol)
        );
  }
}
