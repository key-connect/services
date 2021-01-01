package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseBlockchainConfig;
import app.keyconnect.cli.config.BaseClientConfig;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(
    name = "fees",
    description = "View blockchain fees"
)
public class FeesCommand extends BaseBlockchainConfig implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    System.out.println(
        buildApiClient()
          .getFee(chainId, network)
    );
    return 0;
  }
}
