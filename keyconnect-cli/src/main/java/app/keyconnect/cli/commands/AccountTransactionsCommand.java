package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseAccountBlockchainConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "account-transactions",
    aliases = {"transactions", "txs"},
    description = "Print transactions for a given account / wallet address"
)
public class AccountTransactionsCommand extends BaseAccountBlockchainConfig implements Callable<Integer> {

  @Option(
      names = {"-p", "--page"},
      description = "Cursor / Page information"
  )
  private String page;

//  @Option(
//      names = {"-l", "--limit"},
//      description = "Maximum number of transactions to view"
//  )
//  private String limit;

  @Option(
      names = {"-f", "--fiat"},
      description = "Fiat currency to convert value in",
      required = false
  )
  private String fiat;

  @Override
  public Integer call() throws Exception {
    ConsoleUtil.print(
        getBlockchainApi()
          .getAccountTransactions(chainId, accountAddress, network, page, fiat)
    );
    return 0;
  }
}
