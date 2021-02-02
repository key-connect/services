package app.keyconnect.server.exchanges.services;

import info.bitrich.xchangestream.core.StreamingExchange;
import org.knowm.xchange.dto.marketdata.OrderBook;

public class OrderbookAggregator implements OrderbookConsumer {

  private final OrderbookConsumer[] consumers;

  public OrderbookAggregator(OrderbookConsumer... consumers) {
    this.consumers = consumers;
  }

  public void start() {
    for (OrderbookConsumer consumer : consumers) {
      consumer.start();
    }
  }

  public void stop() {
    if (consumers != null && consumers.length > 0) {
      for (OrderbookConsumer consumer : consumers) {
        consumer.stop();
      }
    }
  }

  @Override
  public OrderBook getOrderBook() {
    return null;
  }

  @Override
  public void consume(Class<? extends StreamingExchange> exchangeClass, OrderBook orderBook) {
// keep track of combined orderbook, individually the consumers keep track of their own
  }
}
