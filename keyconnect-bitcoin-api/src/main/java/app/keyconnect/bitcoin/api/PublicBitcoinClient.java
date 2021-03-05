package app.keyconnect.bitcoin.api;

import app.keyconnect.bitcoin.api.client.model.GetBlockchainInfoRequest;
import app.keyconnect.bitcoin.api.client.model.GetBlockchainInfoResponse;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.CoinSelection;
import org.bitcoinj.wallet.CoinSelector;
import org.bitcoinj.wallet.DefaultCoinSelector;
import org.bitcoinj.wallet.FilteringCoinSelector;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class PublicBitcoinClient {

  private final RestTemplate restTemplate;
  private final PublicBitcoinClientConfig config;
  private final WalletAppKit kit;
  private final NetworkParameters networkParams;

  public PublicBitcoinClient(RestTemplate restTemplate, PublicBitcoinClientConfig config) {
    this.restTemplate = restTemplate;
    this.config = config;
    networkParams = NetworkParameters.fromID(NetworkParameters.ID_MAINNET);
    kit = new WalletAppKit(
        networkParams,
        new File("."),
        "bitcoinj"
    );

    kit.setBlockingStartup(true)
        .setAutoSave(true)
        .startAsync();
  }

  public void destroy() {
    kit.stopAsync();
    kit.awaitTerminated();
  }

  public GetBlockchainInfoResponse getBlockchainInfo() {
    final HttpEntity<GetBlockchainInfoRequest> requestEntity = requestEntity(
        new GetBlockchainInfoRequest()
            .id(UUID.randomUUID().toString())
            .params(Collections.emptyList()));
    return restTemplate
        .postForObject(config.getJsonRpcEndpoint(), requestEntity, GetBlockchainInfoResponse.class);
  }

  public long getBalance(String address) {
    kit.awaitRunning();
    kit.wallet().addWatchedAddress(Address.fromString(networkParams, address));
//    kit.wallet().sc
    final Coin balance = kit.wallet().getBalance();
    return balance.getValue();
  }

  private HttpEntity<GetBlockchainInfoRequest> requestEntity(GetBlockchainInfoRequest request) {
    final HttpHeaders headers = buildHeaders();
    return new HttpEntity<>(request, headers);
  }

  private HttpHeaders buildHeaders() {
    final HttpHeaders headers = new HttpHeaders();
    addAuthHeaders(headers);
    return headers;
  }

  private void addAuthHeaders(HttpHeaders headers) {
    headers.setBasicAuth(config.getUsername(), config.getPassword());
  }
}
