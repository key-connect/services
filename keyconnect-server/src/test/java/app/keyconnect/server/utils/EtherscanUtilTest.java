package app.keyconnect.server.utils;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.server.utils.models.SuccessEtherscanResponse;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

public class EtherscanUtilTest {

  private RestTemplate template;
  private Environment environment;
  private String token;
  private EtherscanUtil subject;

  @Before
  public void setUp() throws Exception {
    template = new RestTemplate();
    environment = new StandardEnvironment();
    token = environment.getProperty("ETHERSCAN_TOKEN");
    subject = new EtherscanUtil(template, token);
  }

  @Test
  public void returnsAllTransactionsForGivenAccount() {
    final SuccessEtherscanResponse transactions = subject
        .getTransactionsForAccount("mainnet", "0x6c8C5d80B9C9C644E342d60Cc904A9D5E3C7a8e3",
            "11511484", "1", "10");
    assertThat(transactions.getResult()).hasSize(10);
    assertThat(transactions.getResult()[0].getHash()).isEqualTo("0x806b44b2fa81527b8038a709b9a69f94254f0f008619372f98c1ced92b05bbdf");
  }

  @Test
  public void returnsAllTokenTransactionsForGivenAccount() {
    final SuccessEtherscanResponse tokenTransactions = subject.getTokenTransactionsForAccount("mainnet", "0x4E83362442B8d1beC281594cEa3050c8EB01311C", "11514883", "1", "10");
    assertThat(tokenTransactions.getResult()).hasSize(10);
  }

  @Test
  public void handlesErrors() {
    try {
      subject
          .getTransactionsForAccount("mainnet", "mytestinvalidaddress", "11511484", "1", "10");
      TestCase.fail("should have thrown 400");
    } catch (ResponseStatusException e) {
      assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
      assertThat(e.getReason()).contains("is invalid on eth blockchain");
      assertThat(e.getReason()).contains("mytestinvalidaddress");
    }
  }
}
