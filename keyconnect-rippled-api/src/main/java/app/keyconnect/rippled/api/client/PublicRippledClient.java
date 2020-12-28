package app.keyconnect.rippled.api.client;

import app.keyconnect.rippled.api.client.config.PublicRippledClientConfig;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import app.keyconnect.rippled.api.client.model.AccountInfoRequest;
import app.keyconnect.rippled.api.client.model.AccountInfoRequestParam;
import app.keyconnect.rippled.api.client.model.AccountInfoResponse;
import app.keyconnect.rippled.api.client.model.AccountTransactionItem;
import app.keyconnect.rippled.api.client.model.AccountTransactionMarker;
import app.keyconnect.rippled.api.client.model.AccountTransactionRequest;
import app.keyconnect.rippled.api.client.model.AccountTransactionRequestParam;
import app.keyconnect.rippled.api.client.model.AccountTransactionResponse;
import app.keyconnect.rippled.api.client.model.FeeRequest;
import app.keyconnect.rippled.api.client.model.FeeResponse;
import app.keyconnect.rippled.api.client.model.ServerInfoRequest;
import app.keyconnect.rippled.api.client.model.ServerInfoResponse;
import app.keyconnect.rippled.api.client.model.SubmitTransactionRequest;
import app.keyconnect.rippled.api.client.model.SubmitTransactionRequestParam;
import app.keyconnect.rippled.api.client.model.SubmitTransactionResponse;
import app.keyconnect.rippled.api.client.model.TransactionRequest;
import app.keyconnect.rippled.api.client.model.TransactionRequestParam;
import app.keyconnect.rippled.api.client.model.TransactionResponse;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import org.springframework.web.client.RestTemplate;

public class PublicRippledClient {

  private final RestTemplate restTemplate;
  private final PublicRippledClientConfig config;

  public PublicRippledClient(RestTemplate restTemplate, PublicRippledClientConfig config) {
    this.restTemplate = restTemplate;
    this.config = config;
  }

  public ServerInfoResponse getServerInfo() {
    return restTemplate.postForObject(config.getJsonRpcEndpoint(), new ServerInfoRequest(),
        ServerInfoResponse.class);
  }

  public FeeResponse getFee() {
    return restTemplate
        .postForObject(config.getJsonRpcEndpoint(), new FeeRequest(), FeeResponse.class);
  }

  public AccountInfoResponse getAccountInfo(String address) {
    final AccountInfoRequest request = new AccountInfoRequest()
        .addParamsItem(
            new AccountInfoRequestParam()
                .account(address)
        );
    return restTemplate
        .postForObject(config.getJsonRpcEndpoint(), request, AccountInfoResponse.class);
  }

  public AccountTransactionResponse getAccountTransactions(String address, int limit) {
    return getAccountTransactions(address, limit, -1, -1, null);
  }

  public AccountTransactionResponse getAccountTransactions(String address, int limit,
      @Nullable AccountTransactionMarker marker) {
    return getAccountTransactions(address, limit, null, null, marker);
  }

  public List<AccountTransactionItem> getAllTransactions(String address) {
    final List<AccountTransactionItem> transactions = new LinkedList<>();
    AccountTransactionResponse response = getAccountTransactions(address, 25, -1, -1, null);
    transactions.addAll(response.getResult().getTransactions());
    if (response.getResult().getMarker() == null) {
      return transactions;
    }
    AccountTransactionMarker marker = response.getResult().getMarker();
    while (marker != null) {
      response = getAccountTransactions(address, 25, null, null, marker);
      marker = response.getResult().getMarker();
    }
    return transactions;
  }

  /**
   * THIS ONE WORKS: FIRST TIME -1, -1 AS LEDGER MIN AND MAX SUBSEQUENT TIMES NULL, NULL AS LEDGER
   * MIN AND MAX, AND MARKER OBJECT FROM PREVIOUS
   */
  public AccountTransactionResponse getAccountTransactions(String address, int limit,
      @Nullable Integer ledgerIndexMax, @Nullable Integer ledgerIndexMin,
      @Nullable AccountTransactionMarker marker) {
    final AccountTransactionRequestParam accountTransactionRequestParam = new AccountTransactionRequestParam()
        .account(address)
        .limit(limit)
        .ledgerIndexMax(ledgerIndexMax)
        .ledgerIndexMin(ledgerIndexMin)
        .marker(marker);

    final AccountTransactionRequest request = new AccountTransactionRequest()
        .addParamsItem(
            accountTransactionRequestParam
        );
    return restTemplate
        .postForObject(config.getJsonRpcEndpoint(), request, AccountTransactionResponse.class);
  }

  public TransactionResponse getTransaction(String hash) {
    final TransactionRequest transactionRequest = new TransactionRequest()
        .addParamsItem(
            new TransactionRequestParam()
                .transaction(hash)
        );
    return restTemplate.postForObject(config.getJsonRpcEndpoint(),
        transactionRequest, TransactionResponse.class);
  }

  public SubmitTransactionResponse submitTransaction(String blob) {
    final SubmitTransactionRequest submitTransactionRequest = new SubmitTransactionRequest()
        .addParamsItem(
            new SubmitTransactionRequestParam()
              .txBlob(blob)
        );
    return restTemplate.postForObject(config.getJsonRpcEndpoint(), submitTransactionRequest,
        SubmitTransactionResponse.class);
  }

  // development only
  private String toJson(Object object) {
    final ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return "";
  }
}
