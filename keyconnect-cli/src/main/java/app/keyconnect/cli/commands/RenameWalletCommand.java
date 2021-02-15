package app.keyconnect.cli.commands;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.sdk.wallets.BlockchainWallet;
import app.keyconnect.sdk.wallets.io.WalletWriter;
import app.keyconnect.cli.utils.LocalWalletData;
import app.keyconnect.cli.utils.LocalWalletHelper;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "rename",
    aliases = "rename-wallet",
    description = "Rename a given local wallet"
)
public class RenameWalletCommand implements Callable<Integer> {

  @Option(
      names = {"-n", "--name"},
      description = "Current name of the wallet to rename",
      required = true
  )
  private String name;

  @Option(
      names = {"--new-name"},
      description = "New name of the wallet to rename the existing wallet to",
      required = true
  )
  private String newName;

  @Option(
      names = {"-c", "--chainid"},
      description = "ID of the blockchain",
      required = true
  )
  protected String chainId;

  @Override
  public Integer call() throws Exception {
    final LocalWalletData localWalletData = LocalWalletHelper.readLocalWallet();
    final List<BlockchainWallet> allWallets = localWalletData.getWallet()
        .getWalletFactory(ChainIdEnum.valueOf(chainId.toUpperCase(Locale.ROOT)))
        .getGeneratedWallets();
    final Optional<BlockchainWallet> maybeExistingWallet = allWallets
        .stream()
        .filter(w -> w.getName().equals(name))
        .findFirst();

    if (maybeExistingWallet.isEmpty()) {
      System.out.println("Local wallet by name " + name + " could not be found.");
      System.exit(1);
    }

    final Optional<BlockchainWallet> newNameMatchingWallet = allWallets.stream()
        .filter(w -> w.getName().equals(newName))
        .findFirst();

    if (newNameMatchingWallet.isPresent()) {
      System.out.println("New name is already taken by an existing wallet. Please choose a different name.");
      System.exit(1);
    }

    final BlockchainWallet wallet = maybeExistingWallet.get();
    wallet.setName(newName);
    new WalletWriter(localWalletData.getWallet()).writeToFile(localWalletData.getWalletFile(),
        localWalletData.getWalletPassword());

    System.out.println("Renamed wallet from " + name + " to " + newName);
    return 0;
  }
}
