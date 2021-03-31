package app.keyconnect.server.exchanges.services;

import static app.keyconnect.server.exchanges.services.utils.OrderBookUtils.indexOrders;

import app.keyconnect.api.client.model.Order;
import app.keyconnect.server.exchanges.ExchangeService;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StreamingOrderBookConsumer implements
    OrderBookConsumer, Observer<OrderBook> {

  private static final Logger logger = LoggerFactory.getLogger(StreamingOrderBookConsumer.class);
  private final Map<BigDecimal, Order> asksByPrice = new ConcurrentHashMap<>();
  private final Map<BigDecimal, Order> bidsByPrice = new ConcurrentHashMap<>();
  private final CurrencyPair currencyPair;
  private final ExchangeService exchangeService;

  public StreamingOrderBookConsumer(ExchangeService exchangeService, CurrencyPair currencyPair) {
    this.exchangeService = exchangeService;
    this.currencyPair = currencyPair;
    exchangeService.subscribeOrderBook(currencyPair, this);
  }

  @Override
  public ExchangeService getExchangeService() {
    return exchangeService;
  }

  @Override
  public List<Order> getAsks() {
    return new ArrayList<>(asksByPrice.values());
  }

  @Override
  public List<Order> getBids() {
    return new ArrayList<>(bidsByPrice.values());
  }

  @Override
  public BigDecimal getAskVolume() {
    return getAsks()
        .parallelStream()
        .map(Order::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public BigDecimal getBidVolume() {
    return getBids()
        .parallelStream()
        .map(Order::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public CurrencyPair getCurrencyPair() {
    return currencyPair;
  }

  @Override
  public void onSubscribe(@NotNull Disposable d) {
    logger
        .info("Subscribed to orderbook, exchange={}, currencyPair={}", getName(), currencyPair);
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
    logger.info("Completed subscription to orderbook, exchange={}, currencyPair={}", getName(),
        currencyPair);
  }
}
