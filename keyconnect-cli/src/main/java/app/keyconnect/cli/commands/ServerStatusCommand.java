package app.keyconnect.cli.commands;

import app.keyconnect.api.client.DefaultApi;
import app.keyconnect.cli.config.BaseClientConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;

@Command(
    name = "server-status",
    aliases = {"server", "srv"},
    description = "Print server status"
)
public class ServerStatusCommand extends BaseClientConfig implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    final DefaultApi api = buildApiClient();

    ConsoleUtil.print(api.getServerStatus());
    return 0;
  }
}
