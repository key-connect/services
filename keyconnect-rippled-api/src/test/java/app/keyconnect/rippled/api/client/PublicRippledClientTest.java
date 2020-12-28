package app.keyconnect.rippled.api.client;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.rippled.api.client.PublicRippledClient;
import app.keyconnect.rippled.api.client.config.PublicRippledClientConfig;
import app.keyconnect.rippled.api.spring.JacksonConfig;
import app.keyconnect.rippled.api.client.model.AccountInfoResponse;
import app.keyconnect.rippled.api.client.model.AccountTransactionResponse;
import app.keyconnect.rippled.api.client.model.FeeResponse;
import app.keyconnect.rippled.api.client.model.ServerInfoResponse;
import app.keyconnect.rippled.api.client.model.TransactionResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class PublicRippledClientTest {

  private static final String RIPPLED_MAINNET_JSONRPC_URI = "https://s2.ripple.com:51234/";
  private PublicRippledClient client;

  @Before
  public void setUp() throws Exception {
    final JacksonConfig jacksonConfig = new JacksonConfig();

    client = new PublicRippledClient(
        (RestTemplate) jacksonConfig.restOperations(
            jacksonConfig.mappingJacksonHttpMessageConverter(jacksonConfig.objectMapper())),
        PublicRippledClientConfig.builder()
            .jsonRpcEndpoint(RIPPLED_MAINNET_JSONRPC_URI)
            .build()
    );
  }

  @Test
  public void getServerInfoReturnsServerInfo() {
    final ServerInfoResponse serverInfoResponse = client.getServerInfo();

    assertThat(serverInfoResponse).isNotNull();
    assertThat(serverInfoResponse.getResult()).isNotNull();
    assertThat(serverInfoResponse.getResult().getStatus()).isEqualTo("success");
  }

  @Test
  public void getFeeReturnsFee() {
    final FeeResponse feeResponse = client.getFee();

    assertThat(feeResponse).isNotNull();
    assertThat(feeResponse.getResult()).isNotNull();
    assertThat(feeResponse.getResult().getStatus()).isEqualTo("success");
  }

  @Test
  public void getAccountInfoResponse() {
    final AccountInfoResponse accountInfoResponse = client
        .getAccountInfo("rDsbeomae4FXwgQTJp9Rs64Qg9vDiTCdBv");

    assertThat(accountInfoResponse).isNotNull();
    assertThat(accountInfoResponse.getResult()).isNotNull();
    assertThat(accountInfoResponse.getResult().getStatus()).isEqualTo("success");
  }

  @Test
  public void getAccountTransactionsResponse() {
    final AccountTransactionResponse accountInfoResponse = client
        .getAccountTransactions("rDsbeomae4FXwgQTJp9Rs64Qg9vDiTCdBv", 4);

    assertThat(accountInfoResponse).isNotNull();
    assertThat(accountInfoResponse.getResult()).isNotNull();
    assertThat(accountInfoResponse.getResult().getStatus()).isEqualTo("success");
  }

  @Test
  public void getAccountTransactionsPaginatedTest() {
    final List<String> txHashes = new ArrayList<>(12);
    AccountTransactionResponse transactions = client
        .getAccountTransactions("rDsbeomae4FXwgQTJp9Rs64Qg9vDiTCdBv",
            2, -1, -1, null
        );
    txHashes.addAll(
        transactions.getResult()
            .getTransactions()
            .stream()
            .map(t -> t.getTx().getHash())
            .collect(Collectors.toList())
    );

    for (int i = 0; i < 5; i++) {
      transactions = client
          .getAccountTransactions("rDsbeomae4FXwgQTJp9Rs64Qg9vDiTCdBv",
              2,
              null,
              null,
              transactions.getResult().getMarker());
      txHashes.addAll(
          transactions.getResult()
              .getTransactions()
              .stream()
              .map(t -> t.getTx().getHash())
              .collect(Collectors.toList())
      );
    }

    assertThat(txHashes.stream().distinct().collect(Collectors.toList()).size()).isEqualTo(12);
  }

  @Test
  public void getTransactionResponse() {
    final TransactionResponse transactionResponse = client
        .getTransaction("2F52A8D679FBF47B698619916C1CA2FA478C73D738597BDFA8D4F138480410B4");

    assertThat(transactionResponse).isNotNull();
    assertThat(transactionResponse.getResult()).isNotNull();
    assertThat(transactionResponse.getResult().getStatus()).isEqualTo("success");
  }
}
