package app.keyconnect.server.exchanges.services;

import static app.keyconnect.server.exchanges.services.utils.OrderBookUtils.indexOrders;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StreamingOrderBookConsumer implements
    OrderBookConsumer, Observer<OrderBook> {

  private static final Logger logger = LoggerFactory.getLogger(StreamingOrderBookConsumer.class);
  private final FanoutObserver<OrderBook> observer;
  private final Map<BigDecimal, LimitOrder> asksByPrice = new TreeMap<>();
  private final Map<BigDecimal, LimitOrder> bidsByPrice = new TreeMap<>();
  private final Class<? extends StreamingExchange> exchangeClass; // or specification below
  private final CurrencyPair currencyPair;
  private ExchangeSpecification specification;
  private StreamingExchange exchange;
  private boolean started = false;

  public StreamingOrderBookConsumer(CurrencyPair currencyPair, Class<? extends StreamingExchange> exchangeClass) {
    this.exchangeClass = exchangeClass;
    this.currencyPair = currencyPair;

    // first observer is always this class
    this.observer = new FanoutObserver<>(this);
  }

  public void setSpecification(ExchangeSpecification specification) {
    this.specification = specification;
  }

  private StreamingExchange createExchange() {
    final StreamingExchange exchange;
    if (specification != null) {
      exchange = StreamingExchangeFactory.INSTANCE
          .createExchange(specification);
    } else {
      exchange = StreamingExchangeFactory.INSTANCE
          .createExchange(exchangeClass);
    }
    return exchange;
  }

  @Override
  public void start() {
    if (null == currencyPair) throw new IllegalStateException("currencyPair must be set to start()");
    if (started) return;

    stop(); // try to stop if its already running
    asksByPrice.clear();
    bidsByPrice.clear();
    // prefer specification if its set, else fallback to class
    exchange = createExchange();

    logger.info("Connecting to exchange, class={}", exchangeClass);

    exchange.connect(ProductSubscription.create()
        .addOrderbook(currencyPair)
        .build())
        .blockingAwait(1, TimeUnit.MINUTES);


    logger.info("Subscribing to orderbook(s)");
    exchange
        .getStreamingMarketDataService()
        .getOrderBook(currencyPair)
        .subscribe(observer);
  }

  public void restart() {
    logger.warn("Restarting orderbook consumer...");
    start();
  }

  @Override
  public void stop() {
    logger.info("Stopping consumer");
    if (exchange != null)
      exchange.disconnect().blockingAwait(1, TimeUnit.MINUTES);
    started = false;
    logger.info("Consumer stopped");
  }

  @Override
  public void subscribe(Observer<OrderBook> observable) {
    this.observer.addObservable(observable);
  }

  @Override
  public List<LimitOrder> getAsks() {
    return new ArrayList<>(asksByPrice.values());
  }

  @Override
  public List<LimitOrder> getBids() {
    return new ArrayList<>(bidsByPrice.values());
  }

  @Override
  public BigDecimal getAskVolume() {
    return getAsks()
        .parallelStream()
        .map(LimitOrder::getRemainingAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public BigDecimal getBidVolume() {
    return getBids()
        .parallelStream()
        .map(LimitOrder::getRemainingAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    return currencyPair;
  }

  @Override
  public void onSubscribe(@NotNull Disposable d) {
    logger
        .info("Subscribed to orderbook, exchange={}, currencyPair={}", exchangeClass, currencyPair);
  }

  @Override
  public void onNext(@NotNull OrderBook orderBook) {
    // index by price
    indexOrders(asksByPrice, orderBook.getAsks());
    indexOrders(bidsByPrice, orderBook.getBids());
  }

  @Override
  public void onError(@NotNull Throwable e) {
    logger.error("Encountered error when subscribed to orderbook", e);
  }

  @Override
  public void onComplete() {
    logger.info("Completed subscription to orderbook, exchange={}, currencyPair={}", exchangeClass,
        currencyPair);
  }
}
