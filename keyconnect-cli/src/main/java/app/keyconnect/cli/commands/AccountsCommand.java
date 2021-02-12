package app.keyconnect.cli.commands;

import app.keyconnect.api.client.model.BlockchainAccountInfo.ChainIdEnum;
import app.keyconnect.api.wallets.BlockchainWallet;
import app.keyconnect.cli.config.BaseBlockchainConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import app.keyconnect.cli.utils.LocalWalletData;
import app.keyconnect.cli.utils.LocalWalletHelper;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "accounts",
    aliases = {"a"},
    description = "Print information for a given account / wallet address"
)
public class AccountsCommand extends BaseBlockchainConfig implements Callable<Integer> {

  @Option(
      names = {"-a", "--account"},
      description = "Account address (to be used in place of --name)",
      required = false
  )
  private String accountAddress;

  @Option(
      names = {"-f", "--fiat"},
      description = "Fiat currency to convert value in",
      required = false
  )
  private String fiat;

  @Option(
      names = {"--name"},
      description = "Local wallet name (to be used in place of --account)",
      required = false
  )
  private String walletName;

  @Override
  public Integer call() throws Exception {
    if (StringUtils.isBlank(accountAddress)
      && StringUtils.isBlank(walletName)) {
      System.out.println("One of --name or --account must be specified");
      System.exit(1);
    }

    final String address;
    if (StringUtils.isNotBlank(walletName)) {
      final Optional<BlockchainWallet> foundWallet = LocalWalletHelper.readLocalWallet()
          .getWallet()
          .getWalletFactory(ChainIdEnum.valueOf(chainId.toLowerCase(Locale.ROOT)))
          .getGeneratedWallets()
          .stream()
          .filter(w -> w.getName().equalsIgnoreCase(walletName))
          .findFirst();

      if (foundWallet.isEmpty()) {
        System.out.println("Local wallet by name " + walletName + " could not be found.");
        System.exit(1);
      }

      address = foundWallet.get().getAddress();
    } else {
      address = accountAddress;
    }

    ConsoleUtil.print(getBlockchainApi()
        .getAccountInfo(chainId, address, network, fiat));
    return 0;
  }
}
