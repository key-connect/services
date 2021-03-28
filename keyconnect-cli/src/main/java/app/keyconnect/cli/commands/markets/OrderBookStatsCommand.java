package app.keyconnect.cli.commands.markets;

import app.keyconnect.api.client.model.Order;
import app.keyconnect.api.client.model.OrderBook;
import app.keyconnect.cli.config.BaseClientConfig;
import app.keyconnect.cli.utils.ConsoleUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "orderbookstats",
    aliases = {"order-book-stats", "orderbook-stats"},
    description = "Displays stats for aggregate order book or for a specific exchange if specified"
)
public class OrderBookStatsCommand extends BaseClientConfig implements Callable<Integer> {

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
    if (orderBook.getAsks() != null) {
      System.out.println("Ask stats");
      System.out.println("=========");
      System.out.println("Total entries: " + orderBook.getAsks().size());
      final BigDecimal totalVolume = orderBook.getAsks()
          .stream()
          .map(Order::getAmount)
          .reduce(BigDecimal::add)
          .orElse(BigDecimal.ZERO);
      System.out.println("Total volume: " + totalVolume.toPlainString());
      final BigDecimal weightedAvgPrice = orderBook.getAsks().stream()
          .map(o -> o.getPrice().multiply(o.getAmount()))
          .reduce(BigDecimal::add)
          .orElse(BigDecimal.ZERO)
          .divide(totalVolume, RoundingMode.HALF_UP);
      System.out.println("Average price: " + weightedAvgPrice);
      System.out.println();
    }

    if (orderBook.getBids() != null) {
      System.out.println("Bid stats");
      System.out.println("=========");
      System.out.println("Total entries: " + orderBook.getBids().size());
      final BigDecimal totalVolume = orderBook.getBids()
          .stream()
          .map(Order::getAmount)
          .reduce(BigDecimal::add)
          .orElse(BigDecimal.ZERO);
      System.out.println("Total volume: " + totalVolume.toPlainString());
      final BigDecimal weightedAvgPrice = orderBook.getBids().stream()
          .map(o -> o.getPrice().multiply(o.getAmount()))
          .reduce(BigDecimal::add)
          .orElse(BigDecimal.ZERO)
          .divide(totalVolume, RoundingMode.HALF_UP);
      System.out.println("Average price: " + weightedAvgPrice);
      System.out.println();
    }

    return 0;
  }
}
