package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseBlockchainConfig;
import java.util.Locale;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "fund",
    aliases = {"f"},
    description = "Funds a given account in test"
)
public class FundCommand extends BaseBlockchainConfig implements Callable<Integer> {

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
    if (!chainId.toLowerCase(Locale.ROOT).equalsIgnoreCase("xrp")) {
      System.out.println("Funding is only supported on xrp testnets at the moment.");
      System.exit(1);
    }

    getBlockchainApi()
        .fundAccount(chainId, accountAddress, network);
    System.out.println("Funding request sent. This may take a few minutes depending on traffic.");
    return 0;
  }
}
