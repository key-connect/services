package app.keyconnect.cli.commands;

import app.keyconnect.sdk.wallets.BlockchainWallet;
import app.keyconnect.cli.config.BaseAccountBlockchainConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "accounts",
    aliases = {"a"},
    description = "Print information for a given account / wallet address"
)
public class AccountsCommand extends BaseAccountBlockchainConfig implements Callable<Integer> {

  @Option(
      names = {"-f", "--fiat"},
      description = "Fiat currency to convert value in",
      required = false
  )
  private String fiat;

  @Override
  public Integer call() throws Exception {
    ConsoleUtil.print(getBlockchainApi()
        .getAccountInfo(chainId, getAccountAddress(), network, fiat));
    return 0;
  }
}
