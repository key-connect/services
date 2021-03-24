package app.keyconnect.server.exchanges.services.aggregators;

import app.keyconnect.server.exchanges.services.OrderBookConsumer;
import app.keyconnect.server.exchanges.services.utils.OrderBookUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBookAggregator implements Observer<OrderBook> {

  private static final Logger logger = LoggerFactory.getLogger(OrderBookAggregator.class);
  private final OrderBookConsumer[] consumers;
  private final CurrencyPair currencyPair;
  private final Map<BigDecimal, LimitOrder> asksByPrice = new TreeMap<>();
  private final Map<BigDecimal, LimitOrder> bidsByPrice = new TreeMap<>();

  public OrderBookAggregator(OrderBookConsumer... consumers) {
    Objects.requireNonNull(consumers, "Orderbook aggregator must have non-null consumer");
    assert consumers.length > 0;
    final Set<CurrencyPair> currencyPairs = Arrays.stream(consumers)
        .map(OrderBookConsumer::getCurrencyPair)
        .collect(Collectors.toSet());
    if (currencyPairs.size() != 1) {
      throw new IllegalStateException("All consumers under an aggregator must have the same currency pair, found=" + currencyPairs);
    }
    this.currencyPair = currencyPairs.iterator().next();
    this.consumers = consumers;
  }

  public void start() {
    logger.info("Starting aggregator for currency pair={}", currencyPair);
    for (OrderBookConsumer consumer : consumers) {
      consumer.start();
      consumer.subscribe(this);
    }
  }

  public void stop() {
    if (consumers.length > 0) {
      for (OrderBookConsumer consumer : consumers) {
        consumer.stop();
      }
    }
  }

  public List<LimitOrder> getAsks() {
    return new ArrayList<>(asksByPrice.values());
  }

  public List<LimitOrder> getBids() {
    return new ArrayList<>(bidsByPrice.values());
  }

  public BigDecimal getAskVolume() {
    return getAsks()
        .parallelStream()
        .map(LimitOrder::getRemainingAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  public BigDecimal getBidVolume() {
    return getBids()
        .parallelStream()
        .map(LimitOrder::getRemainingAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public void onSubscribe(@NotNull Disposable d) {
    // no op
  }

  @Override
  public void onNext(@NotNull OrderBook orderBook) {
    OrderBookUtils.indexOrders(asksByPrice, orderBook.getAsks());
    OrderBookUtils.indexOrders(bidsByPrice, orderBook.getBids());
  }

  @Override
  public void onError(@NotNull Throwable e) {

  }

  @Override
  public void onComplete() {

  }
}
