package app.keyconnect.cli.commands.markets;

import app.keyconnect.api.client.MarketsApi;
import app.keyconnect.api.client.model.MarketsResponse;
import app.keyconnect.cli.config.BaseClientConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "available-markets",
    aliases = "markets",
    description = "Displays available markets and status"
)
public class MarketsCommand extends BaseClientConfig implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    final MarketsResponse availableMarkets = getMarketsApi().getAvailableMarkets();
    ConsoleUtil.print(availableMarkets);
    return 0;
  }
}
