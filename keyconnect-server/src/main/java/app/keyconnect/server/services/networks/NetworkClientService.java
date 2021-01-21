package app.keyconnect.server.services.networks;

import app.keyconnect.server.factories.configuration.BlockchainNetworkConfiguration;
import java.util.List;
import java.util.Set;

public interface NetworkClientService<T> {

  Set<NetworkClient<T>> getAllMatching(String network);
  NetworkClient<T> getFirst(String network);
  T getClientForServer(String serverUrl);
  List<BlockchainNetworkConfiguration> getNetworks();

}
