package app.keyconnect.server.gateways;

import app.keyconnect.api.client.model.BlockchainAccountTransactions;
import app.keyconnect.rippled.api.client.PublicRippledClient;
import app.keyconnect.rippled.api.spring.JacksonConfig;
import app.keyconnect.server.factories.configuration.YamlConfiguration;
import app.keyconnect.server.gateways.exceptions.UnknownNetworkException;
import app.keyconnect.server.services.networks.NetworkClientService;
import app.keyconnect.server.services.networks.XrpNetworkClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class XrpGatewayTest {

  @Autowired
  private YamlConfiguration yamlConfiguration;

  @Test
  void testGetTransactions() {
    final NetworkClientService<PublicRippledClient> networkClientService = new XrpNetworkClientService(
        yamlConfiguration,
        () -> {
          final JacksonConfig jacksonConfig = new JacksonConfig();
          return (RestTemplate) jacksonConfig.restOperations(jacksonConfig.mappingJacksonHttpMessageConverter(jacksonConfig.objectMapper()));
        }
    );
    final XrpGateway gateway = new XrpGateway(
        yamlConfiguration,
        networkClientService
    );
    try {
      final BlockchainAccountTransactions transactions = gateway
          .getTransactions("rDsbeomae4FXwgQTJp9Rs64Qg9vDiTCdBv", "mainnet", 5, null);
      System.out.println(transactions);
    } catch (UnknownNetworkException e) {
      e.printStackTrace();
    }
  }
}
