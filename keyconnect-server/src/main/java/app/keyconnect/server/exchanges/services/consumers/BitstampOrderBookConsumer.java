package app.keyconnect.server.exchanges.services.consumers;

import app.keyconnect.server.exchanges.services.StreamingOrderBookConsumer;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

public class BitstampOrderBookConsumer extends StreamingOrderBookConsumer {

  public static final String NAME = "bitstamp";

  public BitstampOrderBookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, BitstampStreamingExchange.class);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
