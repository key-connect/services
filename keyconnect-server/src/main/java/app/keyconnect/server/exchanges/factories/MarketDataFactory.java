package app.keyconnect.server.exchanges.factories;

import app.keyconnect.server.exchanges.ExchangeService;
import app.keyconnect.server.exchanges.services.ExchangeNameService;
import app.keyconnect.server.exchanges.services.ExchangeOrderBookConsumer;
import app.keyconnect.server.exchanges.services.OrderBookConsumer;
import app.keyconnect.server.exchanges.services.aggregators.OrderBookAggregator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MarketDataFactory {

  private static final Logger logger = LoggerFactory.getLogger(MarketDataFactory.class);
  private final Map<String, List<OrderBookConsumer>> consumersByName = new HashMap<>();
  private final Map<String, ExchangeService> exchangeServiceByName = new HashMap<>();
  private final Map<String, List<CurrencyPair>> currencyPairsByName = new HashMap<>();
  private final Map<CurrencyPair, List<OrderBookConsumer>> consumersByCurrency = new HashMap<>();
  private final Map<CurrencyPair, OrderBookAggregator> aggregators = new HashMap<>();
  private boolean initialised = false;

  @PostConstruct
  public void init() {
    if (initialised) {
      throw new IllegalStateException("Cannot re-initialise when already initialised");
    }
    final String[] exchanges = ExchangeNameService.getKnownExchanges();

    for (String exchange : exchanges) {
      logger.info("Initialising exchange {}", exchange);
      final List<CurrencyPair> pairs = ExchangeNameService.supportedCurrencyPairs(exchange);
      currencyPairsByName.put(exchange, pairs);
      final ExchangeService service = new ExchangeService(exchange,
          ExchangeNameService.mapNameToExchangeClass(exchange));
      exchangeServiceByName.put(exchange, service);

      final List<OrderBookConsumer> consumers = pairs.stream()
          .map(p -> {
            logger.info("Processing pair {} for exchange {}", p, exchange);

            final OrderBookConsumer consumer = new ExchangeOrderBookConsumer(
                exchange,
                service, p);
            if (!consumersByCurrency.containsKey(p)) {
              final List<OrderBookConsumer> newList = new ArrayList<>();
              newList.add(consumer);
              consumersByCurrency.put(p, newList);
            } else {
              consumersByCurrency.get(p).add(consumer);
            }
            return consumer;
          })
          .collect(Collectors.toList());
      consumersByName.put(exchange, consumers);
    }

    logger.info("Processing aggregators");
    consumersByCurrency.forEach((pair, consumers) -> {
      logger.info("Creating aggregator for {} using {} consumers", pair, consumers.size());
      final OrderBookAggregator aggregator = new OrderBookAggregator(
          consumers.toArray(new OrderBookConsumer[0]));
      aggregators.put(pair, aggregator);
    });

    logger.info("Initialising aggregators");
    exchangeServiceByName.values().forEach(ExchangeService::connect);
    initialised = true;
    logger.info("Initialisation complete");
  }

  public List<OrderBookConsumer> getConsumersForName(String name) {
    if (!initialised) {
      throw new IllegalStateException("Not initialised, call init() first");
    }
    return consumersByName.getOrDefault(name, Collections.emptyList());
  }

  public List<OrderBookConsumer> getConsumersForCurrencyPair(CurrencyPair currencyPair) {
    if (!initialised) {
      throw new IllegalStateException("Not initialised, call init() first");
    }
    return consumersByCurrency.getOrDefault(currencyPair, Collections.emptyList());
  }

  public List<ExchangeService> getExchangeServices() {
    return new ArrayList<>(exchangeServiceByName.values());
  }

  public Optional<OrderBookConsumer> getConsumerForNameAndCurrencyPair(String name,
      CurrencyPair currencyPair) {
    if (!initialised) {
      throw new IllegalStateException("Not initialised, call init() first");
    }

    final List<OrderBookConsumer> cs = consumersByName.get(name);
    if (cs == null) {
      return Optional.empty();
    }

    return cs
        .stream()
        .filter(c -> c.getCurrencyPair().equals(currencyPair))
        .findFirst();
  }

  public OrderBookAggregator getAggregatorForCurrencyPair(CurrencyPair currencyPair) {
    if (!initialised) {
      throw new IllegalStateException("Not initialised, call init() first");
    }
    return aggregators.get(currencyPair);
  }

  public List<CurrencyPair> getCurrenciesForName(String name) {
    if (!initialised) {
      throw new IllegalStateException("Not initialised, call init() first");
    }
    return currencyPairsByName.getOrDefault(name, Collections.emptyList());
  }
}
