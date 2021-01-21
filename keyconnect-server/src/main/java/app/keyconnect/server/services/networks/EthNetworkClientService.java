package app.keyconnect.server.services.networks;

import app.keyconnect.server.factories.configuration.BlockchainNetworkConfiguration;
import app.keyconnect.server.factories.configuration.BlockchainsConfiguration;
import app.keyconnect.server.factories.configuration.YamlConfiguration;
import app.keyconnect.server.gateways.EthereumGateway;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

/**
 * {@link NetworkClientService} implementation for Ethereum based on {@link Web3j} client
 */
public class EthNetworkClientService implements NetworkClientService<Web3j> {

  private static final Logger logger = LoggerFactory.getLogger(EthNetworkClientService.class);
  private final BlockchainsConfiguration configuration;
  private final Map<String, Web3j> serverClients;

  public EthNetworkClientService(YamlConfiguration yamlConfiguration) {
    this.configuration = yamlConfiguration.getBlockchains()
        .stream()
        .filter(b -> b.getType().equalsIgnoreCase(EthereumGateway.CHAIN_ID))
        .findFirst()
        .orElse(new BlockchainsConfiguration());

    this.serverClients = new ConcurrentHashMap<>();
    this.configuration.getNetworks()
        .stream()
        .map(BlockchainNetworkConfiguration::getAddress)
        .distinct()
        .forEach(a -> {
          final Web3j client = Web3j.build(new HttpService(a));
          logger.info("Connected to node {}", a);
          this.serverClients.put(a, client);
        });
  }

  public Set<NetworkClient<Web3j>> getAllMatching(String network) {
    final List<BlockchainNetworkConfiguration> networks = this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());

    final Set<NetworkClient<Web3j>> clientSet = new HashSet<>(networks.size());
    for (BlockchainNetworkConfiguration eligibleNetwork : networks) {
      final String serverUrl = eligibleNetwork.getAddress();
      // todo maybe cache the whole thing in `this.serverClients`?
      clientSet.add(new NetworkClient<>(this.serverClients.get(serverUrl), eligibleNetwork));
    }

    return clientSet;
  }

  /**
   * Returns the first client found for a given network
   * @param network Network to return the blockchain client for.
   * @return First Web3j client found for the specified network.
   */
  @Override
  public NetworkClient<Web3j> getFirst(String network) {
    return this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .findFirst()
        .map(n -> new NetworkClient<>(this.serverClients.get(n.getAddress()), n))
        .orElse(null);
  }

  @Override
  public Web3j getClientForServer(String serverUrl) {
    return this.serverClients.get(serverUrl);
  }
}
