package com.turinglabs.keyconnect.server.gateways;

import com.keyconnect.rippled.api.spring.JacksonConfig;
import com.turinglabs.keyconnect.server.factories.configuration.YamlConfiguration;
import com.turinglabs.keyconnect.server.utils.EtherscanUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({JacksonConfig.class, YamlConfiguration.class})
public class EthereumGatewayConfiguration {

  @Bean
  public EtherscanUtil etherscanUtil(Environment env) {
    return new EtherscanUtil(new RestTemplate(), env.getProperty("ETHERSCAN_TOKEN"));
  }

  @Bean("EthereumGateway")
  public EthereumGateway xrpGateway(YamlConfiguration configuration,
      EtherscanUtil etherscanUtil) {
    return new EthereumGateway(configuration, etherscanUtil);
  }

}
