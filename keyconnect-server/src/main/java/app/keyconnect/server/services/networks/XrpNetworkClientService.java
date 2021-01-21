package app.keyconnect.server.services.networks;

import app.keyconnect.rippled.api.client.PublicRippledClient;
import app.keyconnect.rippled.api.client.config.PublicRippledClientConfig;
import app.keyconnect.server.factories.configuration.BlockchainNetworkConfiguration;
import app.keyconnect.server.factories.configuration.BlockchainsConfiguration;
import app.keyconnect.server.factories.configuration.YamlConfiguration;
import app.keyconnect.server.gateways.XrpGateway;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class XrpNetworkClientService implements NetworkClientService<PublicRippledClient> {

  private final BlockchainsConfiguration configuration;
  // key is serverUrl
  private final Map<String, PublicRippledClient> serverClients;

  public XrpNetworkClientService(YamlConfiguration yamlConfiguration,
      Supplier<RestTemplate> restTemplateSupplier) {
    this.configuration = yamlConfiguration.getBlockchains()
        .stream()
        .filter(b -> b.getType().equalsIgnoreCase(XrpGateway.CHAIN_ID))
        .findFirst()
        .orElse(new BlockchainsConfiguration());

    this.serverClients = new ConcurrentHashMap<>(this.configuration.getNetworks().size());

    this.configuration.getNetworks()
        .stream()
        .map(BlockchainNetworkConfiguration::getAddress)
        .distinct()
        .forEach(a -> {
          final PublicRippledClient client = new PublicRippledClient(restTemplateSupplier.get(),
              PublicRippledClientConfig.builder().jsonRpcEndpoint(a).build());
          this.serverClients.put(a, client);
        });
  }

  @Override
  public Set<NetworkClient<PublicRippledClient>> getAllMatching(String network) {
    final List<BlockchainNetworkConfiguration> networks = configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());
    final Set<NetworkClient<PublicRippledClient>> clientSet = new HashSet<>(networks.size());
    for (BlockchainNetworkConfiguration eligibleNetwork : networks) {
      final String serverUrl = eligibleNetwork.getAddress();
      // todo maybe cache the whole thing in `this.serverClients`?
      clientSet.add(new NetworkClient<>(this.serverClients.get(serverUrl), eligibleNetwork));
    }

    return clientSet;
  }

  @Override
  public NetworkClient<PublicRippledClient> getFirst(String network) {
    return this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .findFirst()
        .map(n -> new NetworkClient<>(this.serverClients.get(n.getAddress()), n))
        .orElse(null);
  }

  @Override
  public PublicRippledClient getClientForServer(String serverUrl) {
    return this.serverClients.get(serverUrl);
  }

  @Override
  public List<BlockchainNetworkConfiguration> getNetworks() {
    return this.configuration.getNetworks();
  }
}
