package com.turinglabs.keyconnect.server.factories;

import com.turinglabs.keyconnect.server.factories.configuration.YamlConfiguration;
import com.turinglabs.keyconnect.server.gateways.BlockchainGateway;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.yml")
public class AutoBlockchainGatewayFactory implements
    BlockchainGatewayFactory {

  private final Map<String, BlockchainGateway> gateways;

  public AutoBlockchainGatewayFactory(YamlConfiguration configuration,
      ApplicationContext applicationContext) {
    this.gateways = new HashMap<>(configuration.getBlockchains().size());
    configuration.getBlockchains().forEach(b -> {
      final String gatewayName = b.getGateway();
      final BlockchainGateway gateway = applicationContext
          .getBean(gatewayName, BlockchainGateway.class);
      gateways.put(gateway.getChainId(), gateway);
    });
  }

  @Override
  public BlockchainGateway getGateway(String blockchain) {
    return gateways.get(blockchain);
  }

  @Override
  public String[] getBlockchains() {
    return gateways.keySet().toArray(new String[0]);
  }
}
