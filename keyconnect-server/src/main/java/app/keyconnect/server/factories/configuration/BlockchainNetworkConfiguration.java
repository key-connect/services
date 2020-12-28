package app.keyconnect.server.factories.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainNetworkConfiguration {

  private String group;
  private String mode;
  private String address;
  private String type;
}
