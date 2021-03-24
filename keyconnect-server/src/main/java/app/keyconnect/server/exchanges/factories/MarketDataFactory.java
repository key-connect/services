package app.keyconnect.server.exchanges.factories;

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
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketDataFactory {

  private static final Logger logger = LoggerFactory.getLogger(MarketDataFactory.class);
  private final Map<String, List<OrderBookConsumer>> consumersByName = new HashMap<>();
  private final Map<String, List<CurrencyPair>> currencyPairsByName = new HashMap<>();
  private final Map<CurrencyPair, List<OrderBookConsumer>> consumersByCurrency = new HashMap<>();
  private final Map<CurrencyPair, OrderBookAggregator> aggregators = new HashMap<>();
  private boolean initialised = false;

  public void init() {
    if (initialised) {
      throw new IllegalStateException("Cannot re-initialise when already initialised");
    }
    final String[] exchanges = ExchangeNameService.getKnownExchanges();

    for (String exchange : exchanges) {
      logger.info("Initialising exchange {}", exchange);
      final List<CurrencyPair> pairs = ExchangeNameService.supportedCurrencyPairs(exchange);
      currencyPairsByName.put(exchange, pairs);

      final List<OrderBookConsumer> consumers = pairs.stream()
          .map(p -> {
            logger.info("Processing pair {} for exchange {}", p, exchange);
            final OrderBookConsumer consumer = new ExchangeOrderBookConsumer(
                exchange,
                ExchangeNameService.mapNameToExchangeClass(exchange), p);
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

    logger.info("Starting aggregators");
    aggregators.values().forEach(OrderBookAggregator::start);
    initialised = true;
    logger.info("Initialisation complete");
  }

  public List<OrderBookConsumer> getConsumersForName(String name) {
    if (!initialised) throw new IllegalStateException("Not initialised, call init() first");
    return consumersByName.getOrDefault(name, Collections.emptyList());
  }

  public List<OrderBookConsumer> getConsumersForCurrencyPair(CurrencyPair currencyPair) {
    if (!initialised) throw new IllegalStateException("Not initialised, call init() first");
    return consumersByCurrency.getOrDefault(currencyPair, Collections.emptyList());
  }

  public List<CurrencyPair> getCurrenciesForName(String name) {
    if (!initialised) throw new IllegalStateException("Not initialised, call init() first");
    return currencyPairsByName.getOrDefault(name, Collections.emptyList());
  }
}
