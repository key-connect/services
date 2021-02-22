package app.keyconnect.sdk.wallets.io;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletFile {

  private final Map<String, String> accountIndices = new HashMap<>();
  private String mnemonic;
  private String passphrase;

}
