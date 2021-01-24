package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseBlockchainConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
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
      description = "Account address",
      required = true
  )
  private String accountAddress;

  @Option(
      names = {"-f", "--fiat"},
      description = "Fiat currency to convert value in",
      required = false
  )
  private String fiat;

  @Override
  public Integer call() throws Exception {
    ConsoleUtil.print(getBlockchainApi()
        .getAccountInfo(chainId, accountAddress, network, fiat));
    return 0;
  }
}
