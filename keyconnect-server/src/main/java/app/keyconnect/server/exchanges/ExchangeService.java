package app.keyconnect.server.exchanges;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.ProductSubscription.ProductSubscriptionBuilder;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Observable;
import io.reactivex.Observer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;

public class ExchangeService {

  private final Map<CurrencyPair, List<Observer<OrderBook>>> orderBookSubscriptions = new HashMap<>();
  private ExchangeSpecification exchangeSpec;
  private final String name;
  private Class<? extends StreamingExchange> exchangeClass;
  private boolean init = false;
  private StreamingExchange streamingExchange;

  public ExchangeService(String name, Class<? extends StreamingExchange> exchangeClass) {
    this.name = name;
    this.exchangeClass = exchangeClass;
  }

  public ExchangeService(String name, ExchangeSpecification exchangeSpec) {
    this.name = name;
    this.exchangeSpec = exchangeSpec;
  }

  public String getName() {
    return name;
  }

  public void subscribeOrderBook(CurrencyPair currencyPair, Observer<OrderBook> subscriber) {
    if (init) {
      throw new IllegalStateException("Cannot subscribe to order book after init()");
    }
    orderBookSubscriptions.computeIfAbsent(currencyPair, c -> new ArrayList<>())
        .add(subscriber);
  }

  public List<CurrencyPair> getCurrencies() {
    return new ArrayList<>(orderBookSubscriptions.keySet());
  }

  public boolean isConnected() {
    return streamingExchange.isAlive();
  }

  public void connect() {
    if (init) {
      throw new IllegalStateException("Cannot connect when already initialised");
    }
    ProductSubscriptionBuilder builder = ProductSubscription
        .create();
    orderBookSubscriptions.keySet()
        .forEach(builder::addOrderbook);
    ProductSubscription subscription = builder.build();

    streamingExchange = createExchange();
    streamingExchange.connect(subscription)
        .blockingAwait(1, TimeUnit.MINUTES);

    final StreamingMarketDataService marketDataService = streamingExchange
        .getStreamingMarketDataService();
    orderBookSubscriptions.forEach(
        (c, subscribers) -> {
          final Observable<OrderBook> orderBookObservable = marketDataService.getOrderBook(c);
          subscribers.forEach(orderBookObservable::subscribe);
        }
    );
    init = true;
  }

  @PreDestroy
  public void disconnect() {
    if (!init) {
      throw new IllegalStateException("Cannot disconnect when not connected");
    }
    streamingExchange.disconnect().blockingAwait(1, TimeUnit.MINUTES);
  }

  private StreamingExchange createExchange() {
    final StreamingExchange exchange;
    if (exchangeSpec != null) {
      exchange = StreamingExchangeFactory.INSTANCE
          .createExchange(exchangeSpec);
    } else {
      exchange = StreamingExchangeFactory.INSTANCE
          .createExchange(exchangeClass);
    }
    return exchange;
  }

}
