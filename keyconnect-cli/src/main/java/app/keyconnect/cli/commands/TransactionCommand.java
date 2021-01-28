package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseBlockchainConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "transaction",
    aliases = {"tx", "txn", "hash"},
    description = "Print details of a given transaction ID (hash)"
)
public class TransactionCommand extends BaseBlockchainConfig implements Callable<Integer> {

  @Option(
      names = {"-t", "--tx", "--transaction"},
      description = "Transaction ID",
      required = true
  )
  private String transactionId;

  @Option(
      names = {"-f", "--fiat"},
      description = "Fiat currency to convert value in",
      required = false
  )
  private String fiat;

  @Override
  public Integer call() throws Exception {
    ConsoleUtil.print(getBlockchainApi()
        .getTransaction(chainId, transactionId, network, fiat));
    return 0;
  }
}
