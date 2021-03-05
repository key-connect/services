package app.keyconnect.server.gateways;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

class BitcoinGatewayTest {

  @Test
  void getAccount() {
    final BitcoinJSONRPCClient client = new BitcoinJSONRPCClient(false);
    final String account = client.getAccount("17XiVVooLcdCUCMf9s4t4jTExacxwFS5uh");
    System.out.println(account);
  }
}
