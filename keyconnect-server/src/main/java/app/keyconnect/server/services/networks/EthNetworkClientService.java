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
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class EthNetworkClientService implements NetworkClientService<Web3j> {

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
          this.serverClients.put(a, client);
        });
  }

  public Set<Web3j> getClients(String network) {
    final List<BlockchainNetworkConfiguration> networks = this.configuration.getNetworks()
        .stream()
        .filter(n -> n.getGroup().equalsIgnoreCase(network))
        .collect(Collectors.toList());

    final Set<Web3j> clientSet = new HashSet<>(networks.size());
    for (BlockchainNetworkConfiguration eligibleNetwork : networks) {
      final String serverUrl = eligibleNetwork.getAddress();
      clientSet.add(this.serverClients.get(serverUrl));
    }

    return clientSet;
  }
}
