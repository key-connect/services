package app.keyconnect.cli.commands;

import app.keyconnect.api.wallets.DeterministicWallet;
import app.keyconnect.api.wallets.io.WalletWriter;
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
    System.out.print("Choose a wallet seed passphrase: ");
    final String passphrase = new String(console.readPassword());

    System.out.println();
    System.out.print("Choose wallet password: ");
    final String walletPassword = new String(console.readPassword());

    System.out.println("Creating wallet...");
    final DeterministicWallet wallet = new DeterministicWallet(passphrase);
    final WalletWriter writer = new WalletWriter(wallet);
    writer.writeToFile(walletFile, walletPassword);

    System.out.println("Wallet created.");
    return 0;
  }
}
