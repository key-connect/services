package app.keyconnect.server.exchanges.services;

import info.bitrich.xchangestream.core.StreamingExchange;
import org.knowm.xchange.dto.marketdata.OrderBook;

public interface OrderbookConsumer {

  void start();

  void consume(Class<? extends StreamingExchange> exchangeClass, OrderBook orderBook);

  void stop();

  OrderBook getOrderBook();

}
