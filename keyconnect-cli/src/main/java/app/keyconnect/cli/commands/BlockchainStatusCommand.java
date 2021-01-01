package app.keyconnect.cli.commands;

import app.keyconnect.cli.config.BaseClientConfig;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(
    name = "status",
    aliases = "s",
    description = "Print high-level status of supported block chains"
)
public class BlockchainStatusCommand extends BaseClientConfig implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    System.out.println(
        buildApiClient()
          .getBlockchainsStatus()
    );
    return null;
  }
}
