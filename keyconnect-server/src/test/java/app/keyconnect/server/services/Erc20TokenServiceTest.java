package app.keyconnect.server.services;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import app.keyconnect.server.services.networks.NetworkClientService;
import app.keyconnect.server.utils.EtherscanUtil;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;

class Erc20TokenServiceTest {

  private CredentialsService<Credentials> mockCredentialsService;
  private Erc20TokenService subject;
  private NetworkClientService<Web3j> mockNetworkClientService;

  @BeforeEach
  void setUp() {
    mockCredentialsService = mock(CredentialsService.class);
    mockNetworkClientService = mock(NetworkClientService.class);
    subject = new Erc20TokenService(mockCredentialsService, mockNetworkClientService, mock(
        EtherscanUtil.class));
  }

  @Test
  void availableNetworksAreUnique() {
    final Set<String> availableNetworks = subject.getAvailableNetworks();

    assertThat(availableNetworks).hasSize(2);
    assertThat(availableNetworks).containsExactly("mainnet", "ropsten");
  }
}
