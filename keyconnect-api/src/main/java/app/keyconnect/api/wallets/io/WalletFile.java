package app.keyconnect.api.wallets.io;

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

  private String mnemonic;
  private String passphrase;
  private final Map<String, String> accountIndices = new HashMap<>();

}
