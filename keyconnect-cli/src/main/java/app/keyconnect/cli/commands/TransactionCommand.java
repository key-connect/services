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

  @Override
  public Integer call() throws Exception {
    ConsoleUtil.print(buildApiClient()
        .getTransaction(chainId, transactionId, network));
    return 0;
  }
}
