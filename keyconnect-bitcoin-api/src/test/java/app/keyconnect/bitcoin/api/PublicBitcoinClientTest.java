package app.keyconnect.bitcoin.api;

import static org.junit.Assert.*;

import app.keyconnect.bitcoin.api.client.model.GetBlockchainInfoResponse;
import app.keyconnect.bitcoin.api.spring.JacksonConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class PublicBitcoinClientTest {

  private RestTemplate restTemplate;
  private PublicBitcoinClient subject;

  @Before
  public void setUp() throws Exception {
    restTemplate = JacksonConfig.constructRestTemplate();
    subject = new PublicBitcoinClient(restTemplate, PublicBitcoinClientConfig.builder()
        .jsonRpcEndpoint("http://localhost:8332")
        .username("kcsuser")
        .password("xY5Gd8ZHGx3aLJ-ey7dtCgTKr-Z9")
        .build());
  }

  @Test
  public void getBlockchainInfo() {
    final GetBlockchainInfoResponse info = subject.getBlockchainInfo();
    System.out.println(info);
  }

  @Test
  public void getBalance() {
    final long balance = subject.getBalance("17XiVVooLcdCUCMf9s4t4jTExacxwFS5uh");
    System.out.println(balance);
  }
}
