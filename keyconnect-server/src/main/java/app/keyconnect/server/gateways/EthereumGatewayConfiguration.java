package app.keyconnect.server.gateways;

import app.keyconnect.rippled.api.spring.JacksonConfig;
import app.keyconnect.server.factories.configuration.YamlConfiguration;
import app.keyconnect.server.services.CredentialsService;
import app.keyconnect.server.services.Erc20TokenService;
import app.keyconnect.server.services.EthCredentialsService;
import app.keyconnect.server.services.networks.EthNetworkClientService;
import app.keyconnect.server.services.networks.NetworkClientService;
import app.keyconnect.server.utils.EtherscanUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

@Configuration
@Import({JacksonConfig.class, YamlConfiguration.class})
public class EthereumGatewayConfiguration {

  public static final String BEAN_ETH_CREDENTIALS_SERVICE = "ethCredentialsService";
  public static final String BEAN_ETH_NETWORK_SERVICE = "ethNetworkService";
  public static final String BEAN_ETHEREUM_GATEWAY = "EthereumGateway";

  @Bean
  public EtherscanUtil etherscanUtil(Environment env) {
    return new EtherscanUtil(new RestTemplate(), env.getProperty("ETHERSCAN_TOKEN"));
  }

  @Bean(BEAN_ETH_CREDENTIALS_SERVICE)
  public CredentialsService<Credentials> ethCredentialsService(
      Environment environment) {
    return new EthCredentialsService(environment);
  }

  @Bean(BEAN_ETH_NETWORK_SERVICE)
  public NetworkClientService<Web3j> ethNetworkClientService(
      YamlConfiguration yamlConfiguration) {
    return new EthNetworkClientService(yamlConfiguration);
  }

  @Bean
  public Erc20TokenService erc20TokenService(
      @Qualifier(BEAN_ETH_CREDENTIALS_SERVICE) CredentialsService<Credentials> ethCredentialsService,
      @Qualifier(BEAN_ETH_NETWORK_SERVICE) NetworkClientService<Web3j> ethNetworkClientService,
      EtherscanUtil etherscanUtil) {
    return new Erc20TokenService(ethCredentialsService, ethNetworkClientService, etherscanUtil);
  }

  @Bean(BEAN_ETHEREUM_GATEWAY)
  public EthereumGateway ethereumGateway(YamlConfiguration configuration,
      EtherscanUtil etherscanUtil,
      Erc20TokenService erc20TokenService) {
    return new EthereumGateway(configuration, etherscanUtil, erc20TokenService);
  }
}
