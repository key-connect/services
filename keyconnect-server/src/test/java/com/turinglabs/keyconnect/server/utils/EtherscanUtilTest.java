package com.turinglabs.keyconnect.server.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.turinglabs.keyconnect.server.utils.models.EtherscanResponse;
import org.junit.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.client.RestTemplate;

public class EtherscanUtilTest {

  @Test
  public void returnsAllTransactionsForGivenAccount() {
    final RestTemplate template = new RestTemplate();
    final Environment environment = new StandardEnvironment();
    final String token = environment.getProperty("ETHERSCAN_TOKEN");
    final EtherscanUtil util = new EtherscanUtil(template, token);
    final EtherscanResponse transactions = util
        .getTransactionsForAccount("mainnet", "0x6c8C5d80B9C9C644E342d60Cc904A9D5E3C7a8e3",
            "11511484", "1", "10");
    assertThat(transactions.getResult()).hasSize(10);
    assertThat(transactions.getResult()[0].getHash()).isEqualTo("0x806b44b2fa81527b8038a709b9a69f94254f0f008619372f98c1ced92b05bbdf");
  }
}
