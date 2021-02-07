package app.keyconnect.cli.commands;

import app.keyconnect.api.ApiException;
import app.keyconnect.api.client.model.BlockchainAccountInfo;
import app.keyconnect.api.wallets.BlockchainWallet;
import app.keyconnect.api.wallets.DeterministicWallet;
import app.keyconnect.api.wallets.io.WalletReader;
import app.keyconnect.cli.config.BaseClientConfig;
import java.io.Console;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "wallets",
    description = "View information about local wallets"
)
public class WalletsCommand extends BaseClientConfig implements Callable<Integer> {

  @Option(
      names = {"-c", "--chainid"},
      description = "ID of the blockchain",
      required = false
  )
  protected String chainId;
  @Option(
      names = {"-n", "--network"},
      description = "Blockchain network, if other than server-side default"
  )
  protected String network;
  @Option(
      names = {"-f", "--fiat"},
      description = "Fiat currency to convert value in",
      required = false
  )
  private String fiat;

  @Option(
      names = {"--balances"},
      description = "Show remote balances",
      required = false
  )
  private boolean showBalances;

  @Override
  public Integer call() throws Exception {

    WalletHelper.assertHomeDirectory();
    final File walletFile = WalletHelper.assertWalletFile();

    final Console console = System.console();
    System.out.print("Wallet password: ");
    final String walletPassword = new String(console.readPassword());
//    final String walletPassword = "";
    System.out.println();
    System.out.println("Loading wallet...");
    final DeterministicWallet wallet = WalletReader.fromFile(walletFile, walletPassword);
    wallet.getAllFactories()
        .stream()
        .filter(f -> f.getGeneratedWallets().size() > 0)
        .filter(
            f -> StringUtils.isBlank(chainId) || (StringUtils.isNotBlank(chainId) && f.getChainId()
                .name().equalsIgnoreCase(chainId)))
        .forEach(f -> {
          System.out.println(f.getChainId() + " ===");
          final List<BlockchainWallet> wallets = f.getGeneratedWallets();
          for (int i = 0; i < wallets.size(); i++) {
            BlockchainWallet w = wallets.get(i);
            String fiatInfo = "";
            String balanceAmount = "unavailable";
            if (!showBalances) {
              balanceAmount = "";
              fiatInfo = "";
            } else {
              final BlockchainAccountInfo info;
              try {
                info = getBlockchainApi()
                    .getAccountInfo(f.getChainId().getValue().toLowerCase(Locale.ROOT),
                        w.getAddress(), network, fiat);

                if (info.getBalance() != null) {
                  balanceAmount = f.getChainId().name() + " " + info.getBalance().getAmount();
                }

                if (info.getValue() != null) {
                  fiatInfo = info.getValue().getCurrency() + " " + info.getValue().getAmount();
                }
              } catch (ApiException e) {
                // skip
              }
            }

            System.out.println(i + " " + w.getAddress() + " " + balanceAmount + " " + fiatInfo);
          }
        });
    return 0;
  }
}
