package app.keyconnect.cli.utils;

import app.keyconnect.sdk.wallets.DeterministicWallet;
import java.io.File;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocalWalletData {

  private File walletFile;
  private String walletPassword;
  private DeterministicWallet wallet;

}
