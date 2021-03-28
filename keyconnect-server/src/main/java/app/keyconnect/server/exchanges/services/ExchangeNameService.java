package app.keyconnect.server.exchanges.services;

import app.keyconnect.server.exchanges.services.consumers.BitfinexOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.BitstampOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.CoinbaseProOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.KrakenOrderBookConsumer;
import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import java.util.List;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;

public class ExchangeNameService {

  public static final String[] KNOWN_EXCHANGES = new String[]{BitfinexOrderBookConsumer.NAME,
      BitstampOrderBookConsumer.NAME, CoinbaseProOrderBookConsumer.NAME,
      KrakenOrderBookConsumer.NAME};

  public static String[] getKnownExchanges() {
    return KNOWN_EXCHANGES;
  }

  public static List<CurrencyPair> supportedCurrencyPairs(String name) {
    final Class<? extends StreamingExchange> exchangeClass = mapNameToExchangeClass(name);

    final ExchangeSpecification spec = new ExchangeSpecification(exchangeClass);
    spec.setShouldLoadRemoteMetaData(true);

    final StreamingExchange exchange = StreamingExchangeFactory.INSTANCE
        .createExchange(spec);// fix for binance with exchange specification
    return exchange.getExchangeSymbols();
  }

  public static Class<? extends StreamingExchange> mapNameToExchangeClass(String name) {
    switch (name) {
      case BitfinexOrderBookConsumer.NAME:
        return BitfinexStreamingExchange.class;

      case BitstampOrderBookConsumer.NAME:
        return BitstampStreamingExchange.class;

      case CoinbaseProOrderBookConsumer.NAME:
        return CoinbaseProStreamingExchange.class;

      case KrakenOrderBookConsumer.NAME:
        return KrakenStreamingExchange.class;

      default:
        throw new IllegalArgumentException("Consumer name " + name + " is not recognised");
    }
  }

}
