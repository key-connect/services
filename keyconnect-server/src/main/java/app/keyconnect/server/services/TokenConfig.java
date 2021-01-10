package app.keyconnect.server.services;

import java.util.Map;
import lombok.Data;

@Data
public class TokenConfig {

  /*
  Format is:
    token:
      mainnet: "0xcontracthash"
      ropsten: "0xothercontracthash"
   */
  private Map<String, Map<String, String>> tokens;

}
