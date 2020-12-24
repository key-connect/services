package com.turinglabs.keyconnect.server.gateways;

import com.keyconnect.rippled.api.spring.JacksonConfig;
import com.turinglabs.keyconnect.api.client.model.BlockchainAccountTransactions;
import com.turinglabs.keyconnect.server.factories.configuration.YamlConfiguration;
import com.turinglabs.keyconnect.server.gateways.exceptions.UnknownNetworkException;
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
    final XrpGateway gateway = new XrpGateway(
        yamlConfiguration,
        () -> {
          final JacksonConfig jacksonConfig = new JacksonConfig();
          return (RestTemplate) jacksonConfig.restOperations(jacksonConfig.mappingJacksonHttpMessageConverter(jacksonConfig.objectMapper()));
        }
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
