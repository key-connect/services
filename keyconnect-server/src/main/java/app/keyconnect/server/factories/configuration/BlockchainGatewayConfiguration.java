package app.keyconnect.server.factories.configuration;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlockchainGatewayConfiguration {

  private String gateway;
  private List<BlockchainNetworkConfiguration> networks;
}
