package app.keyconnect.cli.commands;

import app.keyconnect.sdk.wallets.DeterministicWallet;
import app.keyconnect.sdk.wallets.io.WalletWriter;
import java.io.Console;
import java.io.File;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(
    name = "init",
    description = "Initialize local wallet"
)
public class InitCommand implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    WalletHelper.assertHomeDirectory();

    final Console console = System.console();
    final File walletFile = WalletHelper.assertWalletFile();
    System.out.println("You can choose a wallet seed passphrase now. A wallet seed "
        + "passphrase is a piece of string used to generate the mnemonic. You will need "
        + "to remember this passphrase when recovering the wallet, along with the mnemonic. "
        + "It adds extra randomness that is used to generate the wallet keys.");
    String passphrase;
    String confirmPassphrase;
    do {
      System.out.print("Choose a wallet seed passphrase: ");
      passphrase = new String(console.readPassword());
      System.out.print("Confirm wallet seed passphrase: ");
      confirmPassphrase = new String(console.readPassword());

      if (passphrase.equals(confirmPassphrase)) {
        break;
      }

      System.err.println("Passphrases don't match. Try again.");
    } while (true);

    System.out.println();

    String walletPassword;
    String confirmedPassword;
    do {
      System.out.print("Choose wallet password: ");
      walletPassword = new String(console.readPassword());

      System.out.print("Confirm wallet password: ");
      confirmedPassword = new String(console.readPassword());

      if (walletPassword.equals(confirmedPassword)) {
        break;
      }

      System.err.println("Passwords don't match. Try again.");
    } while (true);

    System.out.println("Creating wallet...");
    final DeterministicWallet wallet = new DeterministicWallet(passphrase);
    final WalletWriter writer = new WalletWriter(wallet);
    writer.writeToFile(walletFile, walletPassword);

    System.out.println("Wallet created.");
    return 0;
  }
}
