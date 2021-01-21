package app.keyconnect.server.services.networks;

import app.keyconnect.server.factories.configuration.BlockchainNetworkConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NetworkClient<T> {

  private final T client;
  private final BlockchainNetworkConfiguration network;

}
