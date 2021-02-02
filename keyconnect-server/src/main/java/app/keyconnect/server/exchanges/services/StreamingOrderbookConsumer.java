package app.keyconnect.server.exchanges.services;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamingOrderbookConsumer implements
    OrderbookConsumer {

  private static final Logger logger = LoggerFactory.getLogger(StreamingOrderbookConsumer.class);
  private final CurrencyPair currencyPair;
  private final Class<? extends StreamingExchange> exchangeClass;
  private final AtomicReference<OrderBook> orderBook = new AtomicReference<>();
  private Disposable subscription;
  private StreamingExchange exchange;

  public StreamingOrderbookConsumer(CurrencyPair currencyPair, Class<? extends StreamingExchange> exchangeClass) {
    this.currencyPair = currencyPair;
    this.exchangeClass = exchangeClass;
  }

  @Override
  public void start() {
    exchange = StreamingExchangeFactory.INSTANCE
        .createExchange(exchangeClass);

    logger.info("Connecting to exchange, class={}", exchangeClass);
    exchange.connect(ProductSubscription.create()
    .addOrderbook(currencyPair)
    .build())
    .blockingAwait(1, TimeUnit.MINUTES);

    logger.info("Subscribing to orderbook(s)");
    subscription = exchange
        .getStreamingMarketDataService()
        .getOrderBook(currencyPair)
        .subscribe(this::consumeInternal, this::logError, this::restart);
  }

  public final void consumeInternal(OrderBook orderBook) {
    this.orderBook.set(orderBook);
    consume(orderBook);
  }

  @Override
  public void consume(Class<? extends StreamingExchange> exchangeClass, OrderBook orderBook) {
    // override me
  }

  public void restart() {
    logger.warn("Restarting orderbook consumer...");
    start();
  }

  private void logError(Throwable throwable) {
    logger.warn("Error in consuming orderbook", throwable);
  }

  @Override
  public void stop() {
    if (subscription != null && !subscription.isDisposed()) {
      subscription.dispose();
    }
  }

  @Override
  public OrderBook getOrderBook() {
    return orderBook.get();
  }
}
