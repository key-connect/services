package app.keyconnect.cli.commands;

import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.SystemUtils;

public class WalletHelper {

  private static final String WALLET_FILENAME = ".wallet";
  private static final String HOME_DIRNAME = ".kc";
  public static final String DEFAULT_HOME_DIR_PATH = SystemUtils.getUserHome() + File.pathSeparator + HOME_DIRNAME;
  public static final String DEFAULT_WALLET_PATH = DEFAULT_HOME_DIR_PATH + File.pathSeparator + WALLET_FILENAME;

  public static File assertWalletFile() throws IOException {
    final File walletFile =  new File(WalletHelper.DEFAULT_WALLET_PATH);
    if (walletFile.exists()) {
      System.out.println("Refusing to overwrite existing wallet at " + walletFile.getAbsolutePath());
      System.out.println("Please delete the wallet manually and try again.");
      System.exit(1);
    }

    if (!walletFile.exists() && !walletFile.createNewFile()) {
      System.out.println("Could not create wallet file " + walletFile.getAbsolutePath());
      System.exit(1);
    }

    return walletFile;
  }

  public static File assertHomeDirectory() {
    final File kcDir = new File(WalletHelper.DEFAULT_HOME_DIR_PATH);
    if (!kcDir.exists() && !kcDir.mkdirs()) {
      System.out.println("Could not create home directory at " + kcDir.getAbsolutePath());
      System.exit(1);
    }
    return kcDir;
  }

}
