package app.keyconnect.bitcoin.api;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PublicBitcoinClientConfig {

  private String jsonRpcEndpoint;
  private String username;
  private String password;

}
