package app.keyconnect.cli.commands;

import app.keyconnect.api.client.RatesApi;
import app.keyconnect.cli.config.BaseClientConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "rate",
    description = "Currency conversion rates"
)
public class RateCommand extends BaseClientConfig implements Callable<Integer> {

  @Option(
      names = {"-b", "--base"},
      description = "Base currency",
      required = true
  )
  protected String base;

  @Option(
      names = {"-c", "--counter"},
      description = "Counter currency",
      required = true
  )
  protected String counter;

  @Override
  public Integer call() throws Exception {
    final RatesApi api = getRatesApi();

    ConsoleUtil.print(api.getRate(base, counter));
    return 0;
  }
}
