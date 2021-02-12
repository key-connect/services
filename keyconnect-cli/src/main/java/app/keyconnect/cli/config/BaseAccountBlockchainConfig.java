package app.keyconnect.cli.config;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.api.wallets.BlockchainWallet;
import app.keyconnect.cli.utils.LocalWalletHelper;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Option;

public class BaseAccountBlockchainConfig extends BaseBlockchainConfig {

  @Option(
      names = {"-a", "--account"},
      description = "Account address (not required if --name is specified)",
      required = false
  )
  private String accountAddress;

  @Option(
      names = {"--name"},
      description = "Local wallet name (not required if --account is specified)",
      required = false
  )
  private String walletName;

  public String getAccountAddress() {
    if (StringUtils.isBlank(accountAddress)
        && StringUtils.isBlank(walletName)) {
      System.out.println("One of --name or --account must be specified");
      System.exit(1);
    }

    final String address;
    if (StringUtils.isNotBlank(walletName)) {
      Optional<BlockchainWallet> foundWallet = Optional.empty();
      try {
        foundWallet = LocalWalletHelper.readLocalWallet()
            .getWallet()
            .getWalletFactory(ChainIdEnum.valueOf(chainId.toUpperCase(Locale.ROOT)))
            .getGeneratedWallets()
            .stream()
            .filter(w -> w.getName().equalsIgnoreCase(walletName))
            .findFirst();
      } catch (IOException e) {
        System.out.println(e.getMessage());
        System.out.println("There was a problem reading the local wallet.");
        System.exit(1);
      }

      if (foundWallet.isEmpty()) {
        System.out.println("Local wallet by name " + walletName + " could not be found.");
        System.exit(1);
      }

      address = foundWallet.get().getAddress();
    } else {
      address = accountAddress;
    }

    return address;
  }
}
