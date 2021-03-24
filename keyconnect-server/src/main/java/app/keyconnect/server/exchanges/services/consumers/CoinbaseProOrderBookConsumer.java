package app.keyconnect.server.exchanges.services.consumers;

import app.keyconnect.server.exchanges.services.StreamingOrderBookConsumer;
import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

public class CoinbaseProOrderBookConsumer extends StreamingOrderBookConsumer {

  public static final String NAME = "coinbasepro";

  public CoinbaseProOrderBookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, CoinbaseProStreamingExchange.class);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
