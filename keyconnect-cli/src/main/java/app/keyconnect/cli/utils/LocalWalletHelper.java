package app.keyconnect.cli.utils;

import app.keyconnect.api.wallets.DeterministicWallet;
import app.keyconnect.api.wallets.io.WalletReader;
import app.keyconnect.cli.commands.WalletHelper;
import java.io.Console;
import java.io.File;
import java.io.IOException;

public class LocalWalletHelper {

  public static LocalWalletData readLocalWallet() throws IOException {
    WalletHelper.assertHomeDirectory();
    final File walletFile = WalletHelper.assertWalletFile();

    final Console console = System.console();
    System.out.print("Wallet password: ");
    final String walletPassword = new String(console.readPassword());

    System.out.println();
    System.out.println("Loading wallet...");
    return LocalWalletData.builder()
        .wallet(WalletReader.fromFile(walletFile, walletPassword))
        .walletFile(walletFile)
        .walletPassword(walletPassword)
        .build();
  }

}
