package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseBlockchainConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(
    name = "fees",
    description = "Print fees for a given blockchain"
)
public class FeesCommand extends BaseBlockchainConfig implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    ConsoleUtil.print(
        buildApiClient()
          .getFee(chainId, network)
    );
    return 0;
  }
}
