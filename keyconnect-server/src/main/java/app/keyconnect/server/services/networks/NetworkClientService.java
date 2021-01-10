package app.keyconnect.server.services.networks;

import java.util.Set;

public interface NetworkClientService<T> {

  Set<T> getClients(String network);

}
