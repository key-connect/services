package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseAccountBlockchainConfig;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "account-transactions",
    aliases = {"transactions", "txs"},
    description = "Prints transactions for a given account"
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

  @Override
  public Integer call() throws Exception {
    System.out.println(
        buildApiClient()
          .getAccountTransactions(chainId, accountAddress, network, page)
    );
    return 0;
  }
}
