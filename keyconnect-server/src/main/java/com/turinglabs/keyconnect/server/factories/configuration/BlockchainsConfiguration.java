package com.turinglabs.keyconnect.server.factories.configuration;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockchainsConfiguration {

  private String type;
  private String gateway;
  private List<BlockchainNetworkConfiguration> networks;

}
