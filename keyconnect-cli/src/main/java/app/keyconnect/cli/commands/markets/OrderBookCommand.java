package app.keyconnect.cli.commands.markets;

import app.keyconnect.api.client.model.MarketsResponse;
import app.keyconnect.api.client.model.OrderBook;
import app.keyconnect.cli.config.BaseClientConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "orderbook",
    aliases = "order-book",
    description = "Displays aggregate order book or for a specific exchange if specified"
)
public class OrderBookCommand extends BaseClientConfig implements Callable<Integer> {

  @Option(
      names = {"-b", "--base"},
      required = true
  ) String base;

  @Option(
      names = {"-c", "--counter"},
      required = true
  ) String counter;

  @Option(
      names = {"-e", "--exchange"},
      required = false
  ) String exchange;

  @Override
  public Integer call() throws Exception {
    final OrderBook orderBook;
    if (StringUtils.isNotBlank(exchange)) {
      orderBook = getMarketsApi().getExchangeOrderBook(base, counter, exchange);
    } else {
      orderBook = getMarketsApi().getAggregatedOrderBook(base, counter);
    }
    ConsoleUtil.print(orderBook);
    return 0;
  }
}
