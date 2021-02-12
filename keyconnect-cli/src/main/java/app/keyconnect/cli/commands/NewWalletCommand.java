package app.keyconnect.cli.commands;

import static app.keyconnect.cli.utils.LocalWalletHelper.readLocalWallet;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.api.wallets.BlockchainWallet;
import app.keyconnect.api.wallets.BlockchainWalletFactory;
import app.keyconnect.api.wallets.DeterministicWallet;
import app.keyconnect.api.wallets.io.WalletReader;
import app.keyconnect.api.wallets.io.WalletWriter;
import app.keyconnect.cli.utils.LocalWalletData;
import java.io.Console;
import java.io.File;
import java.util.Locale;
import java.util.Optional;
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

    final LocalWalletData localWalletData = readLocalWallet();
    final BlockchainWalletFactory walletFactory = localWalletData.getWallet().getWalletFactory(chainId);
    final Optional<BlockchainWallet> maybeExistingWallet = walletFactory.getGeneratedWallets()
        .stream()
        .filter(w -> w.getName().equalsIgnoreCase(name))
        .findFirst();

    if (maybeExistingWallet.isPresent()) {
      System.out.println("Wallet already exists with name " + name + ". Please choose a different name for your new wallet.");
      System.exit(1);
    }

    final BlockchainWallet newWallet = walletFactory.generateNext(name);
    System.out.println("Generated wallet: " + newWallet.getAddress());
    new WalletWriter(localWalletData.getWallet()).writeToFile(localWalletData.getWalletFile(),
        localWalletData.getWalletPassword());
    return 0;
  }
}
