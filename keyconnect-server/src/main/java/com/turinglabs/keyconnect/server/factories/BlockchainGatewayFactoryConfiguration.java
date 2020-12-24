package com.turinglabs.keyconnect.server.factories;

import com.turinglabs.keyconnect.server.factories.configuration.YamlConfiguration;
import com.turinglabs.keyconnect.server.gateways.XrpGatewayConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({XrpGatewayConfiguration.class, YamlConfiguration.class})
// todo group all gateway configurations into one and import it as whole here
public class BlockchainGatewayFactoryConfiguration {

  @Bean
  public BlockchainGatewayFactory gatewayFactory(YamlConfiguration configuration,
      ApplicationContext applicationContext) {
    return new AutoBlockchainGatewayFactory(configuration, applicationContext);
  }
}
