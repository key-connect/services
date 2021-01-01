package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseBlockchainConfig;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "accounts",
    aliases = {"a"},
    description = "Prints information for a given account"
)
public class AccountsCommand extends BaseBlockchainConfig implements Callable<Integer> {

  @Option(
      names = {"-a", "--account"},
      description = "Account address",
      required = true
  )
  private String accountAddress;

  @Override
  public Integer call() throws Exception {
    System.out.println(buildApiClient()
        .getAccountInfo(chainId, accountAddress, network));
    return 0;
  }
}
