package app.keyconnect.server.utils;

import app.keyconnect.server.utils.models.BaseEtherscanResponse;
import app.keyconnect.server.utils.models.ErrorEtherscanResponse;
import app.keyconnect.server.utils.models.EtherscanAccountTransaction;
import app.keyconnect.server.utils.models.RawEtherscanResponse;
import app.keyconnect.server.utils.models.SuccessEtherscanResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

// Hack implementation until we get indexing ready to get all transactions
public class EtherscanUtil {

  private static final Logger logger = LoggerFactory.getLogger(EtherscanUtil.class);
  private final RestTemplate restTemplate;
  private final String token;
  private final RateLimiter rateLimiter;
  private static final String ETHERSCAN_MAINNET_HOST = "https://api.etherscan.io";
  private static final String ETHERSCAN_ROPSTEN_HOST = "https://api-ropsten.etherscan.io";
  public static final String ETHERSCAN_TXN_BASE_URI = "/api?module=account&action=txlist&address={address}&startblock=0&endblock={endBlock}&sort=desc&page={page}&offset={offset}&apikey={token}";
  public static final String ETHERSCAN_TOKEN_TXN_BASE_URI = "/api?module=account&action=tokentx&address={address}&startblock=0&endblock={endBlock}&sort=desc&page={page}&offset={offset}&apikey={token}";
  private ObjectMapper mapper;

  public EtherscanUtil(RestTemplate restTemplate, String token) {
    this.restTemplate = restTemplate;
    this.token = token;
    rateLimiter = RateLimiter.create(4);
    mapper = new ObjectMapper();
  }

  public SuccessEtherscanResponse getTransactionsForAccount(String network, String address,
      String latestBlock, String pageNumber, String pageSize) {
    // we only support mainnet for now
    if (!network.equalsIgnoreCase("mainnet") && !network.equalsIgnoreCase("ropsten")) {
      logger.warn("skipping loading of token transactions for non-mainnet and non-ropsten networks");
      return new SuccessEtherscanResponse();
    }

    return acquirePermitAndExecute(() -> {
      final String host;
      if (network.equalsIgnoreCase("mainnet")) {
        host = ETHERSCAN_MAINNET_HOST;
      } else if (network.equalsIgnoreCase("ropsten")) {
        host = ETHERSCAN_ROPSTEN_HOST;
      } else {
        throw new RuntimeException(network + " network is invalid for token transactions, should not have reached here without validation");
      }

      final ResponseEntity<String> response = restTemplate
          .getForEntity(host + ETHERSCAN_TXN_BASE_URI, String.class, address, latestBlock,
              pageNumber, pageSize, token);

      return extractSuccessResponse(address, response);
    });
  }

  private SuccessEtherscanResponse extractSuccessResponse(String address,
      ResponseEntity<String> response) {
    try {
      return mapper.readValue(response.getBody(), SuccessEtherscanResponse.class);
    } catch (JsonProcessingException e) {
      try {
        final ErrorEtherscanResponse errorResponse = mapper
            .readValue(response.getBody(), ErrorEtherscanResponse.class);
        if (errorResponse.getResult().contains("Invalid address format")) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested account " + address + " is invalid on eth blockchain");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorResponse.getResult());
      } catch (JsonProcessingException jsonProcessingException) {
        logger.error("Error processing error response from etherscan", jsonProcessingException);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unidentified error processing transactions");
      }
    }
  }

  public SuccessEtherscanResponse getTokenTransactionsForAccount(String network, String address, String latestBlock, String pageNumber, String pageSize) {
    // we only support mainnet for now
    if (!network.equalsIgnoreCase("mainnet") && !network.equalsIgnoreCase("ropsten")) {
      logger.warn("skipping loading of token transactions for non-mainnet and non-ropsten networks");
      return new SuccessEtherscanResponse();
    }

    return acquirePermitAndExecute(() -> {
      final String host;
      if (network.equalsIgnoreCase("mainnet")) {
        host = ETHERSCAN_MAINNET_HOST;
      } else if (network.equalsIgnoreCase("ropsten")) {
        host = ETHERSCAN_ROPSTEN_HOST;
      } else {
        throw new RuntimeException(network + " network is invalid for token transactions, should not have reached here without validation");
      }

      final ResponseEntity<String> response = restTemplate
          .getForEntity(host + ETHERSCAN_TOKEN_TXN_BASE_URI, String.class, address, latestBlock,
              pageNumber, pageSize, token);
      return extractSuccessResponse(address, response);
    });
  }

  private <T> T acquirePermitAndExecute(Supplier<T> s) {
    final boolean acquired = rateLimiter.tryAcquire(30, TimeUnit.SECONDS);
    if (acquired) {
      return s.get();
    }
    throw new IllegalStateException("Timeout while waiting >30 seconds for permit");
  }
}
