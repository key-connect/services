package app.keyconnect.server.exchanges.services;

import static app.keyconnect.server.exchanges.services.ExchangeNameService.KNOWN_EXCHANGES;
import static app.keyconnect.server.exchanges.services.ExchangeNameService.mapNameToExchangeClass;

import app.keyconnect.server.exchanges.services.consumers.BitfinexOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.BitstampOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.CoinbaseProOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.KrakenOrderBookConsumer;
import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.knowm.xchange.currency.CurrencyPair;

public class OrderBookConsumerFactory {

  private static final Map<String, List<OrderBookConsumer>> consumers = new HashMap<>();

  public OrderBookConsumer consumer(String name, CurrencyPair currencyPair) {
    final List<OrderBookConsumer> consumers = OrderBookConsumerFactory.consumers
        .computeIfAbsent(name, n -> new LinkedList<>());

    final Optional<OrderBookConsumer> maybeConsumer = consumers
        .stream()
        .filter(c -> c.getCurrencyPair().equals(currencyPair))
        .findFirst();

    if (maybeConsumer.isEmpty()) {
      final ExchangeOrderBookConsumer newConsumer = new ExchangeOrderBookConsumer(name,
          mapNameToExchangeClass(name), currencyPair);
      consumers.add(
          newConsumer
      );
      return newConsumer;
    }

    return maybeConsumer.get();
  }
}
