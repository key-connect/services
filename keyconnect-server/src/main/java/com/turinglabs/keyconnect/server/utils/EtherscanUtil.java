package com.turinglabs.keyconnect.server.utils;

import com.google.common.util.concurrent.RateLimiter;
import com.turinglabs.keyconnect.server.utils.models.EtherscanResponse;
import java.util.concurrent.TimeUnit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

// Hack implementation until we get indexing ready to get all transactions
public class EtherscanUtil {

  private final RestTemplate restTemplate;
  private final String token;
  private final RateLimiter rateLimiter;
  public static final String ETHERSCAN_TXN_BASE_URI = "https://api.etherscan.io/api?module=account&action=txlist&address={address}&startblock=0&endblock={endBlock}&sort=desc&page={page}&offset={offset}&apikey={token}";

  public EtherscanUtil(RestTemplate restTemplate, String token) {
    this.restTemplate = restTemplate;
    this.token = token;
    rateLimiter = RateLimiter.create(4);
  }

  public EtherscanResponse getTransactionsForAccount(String network, String address,
      String latestBlock, String pageNumber, String pageSize) {
    // we only support mainnet for now
    if (!network.equalsIgnoreCase("mainnet")) {
      return new EtherscanResponse();
    }

    final boolean acquired = rateLimiter.tryAcquire(30, TimeUnit.SECONDS);
    final ResponseEntity<EtherscanResponse> response;
    if (acquired) {
      response = restTemplate
          .getForEntity(ETHERSCAN_TXN_BASE_URI, EtherscanResponse.class, address, latestBlock,
              pageNumber, pageSize, token);
      return response.getBody();
    }

    throw new IllegalStateException("Timeout while waiting >30 seconds for permit");
  }
}
