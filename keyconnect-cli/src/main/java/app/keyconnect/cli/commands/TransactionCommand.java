package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseBlockchainConfig;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "transaction",
    aliases = {"tx", "txn"},
    description = "Prints details of a given transaction"
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
    System.out.println(buildApiClient()
        .getTransaction(chainId, transactionId, network));
    return 0;
  }
}
