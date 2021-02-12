package app.keyconnect.cli.commands;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.api.wallets.BlockchainWallet;
import app.keyconnect.api.wallets.BlockchainWalletFactory;
import app.keyconnect.api.wallets.DeterministicWallet;
import app.keyconnect.api.wallets.io.WalletReader;
import app.keyconnect.api.wallets.io.WalletWriter;
import java.io.Console;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "new",
    description = "Creates a new wallet"
)
public class NewWalletCommand implements Callable<Integer> {

  @Option(
      names = {"-c", "--chainid"},
      description = "Wallet blockchain ID, one of eth/xrp"
  )
  String chainId;

  @Option(
      names = {"-n", "--name"},
      description = "Memorable name of the wallet"
  )
  private String name;

  @Override
  public Integer call() throws Exception {
    final ChainIdEnum chainId = ChainIdEnum.valueOf(this.chainId.toUpperCase(Locale.ROOT));

    WalletHelper.assertHomeDirectory();
    final File walletFile = WalletHelper.assertWalletFile();
    final Console console = System.console();
    System.out.print("Wallet password: ");
    final String walletPassword = new String(console.readPassword());
    System.out.println();
    System.out.println("Loading wallet...");
    final DeterministicWallet wallet = WalletReader.fromFile(walletFile, walletPassword);
    final BlockchainWalletFactory walletFactory = wallet.getWalletFactory(chainId);
    final BlockchainWallet newWallet = walletFactory.generateNext(name);
    System.out.println("Generated wallet: " + newWallet.getAddress());
    new WalletWriter(wallet).writeToFile(walletFile, walletPassword);
    return 0;
  }
}
