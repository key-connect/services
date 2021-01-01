package app.keyconnect.cli.commands;

import app.keyconnect.api.client.DefaultApi;
import app.keyconnect.cli.config.BaseClientConfig;
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

    System.out.println(api.getServerStatus());
    return 0;
  }
}
