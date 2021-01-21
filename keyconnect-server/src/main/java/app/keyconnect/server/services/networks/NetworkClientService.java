package app.keyconnect.server.services.networks;

import java.util.Set;

public interface NetworkClientService<T> {

  Set<NetworkClient<T>> getAllMatching(String network);
  NetworkClient<T> getFirst(String network);
  T getClientForServer(String serverUrl);

}
