package app.keyconnect.server.exchanges.services.consumers;

import app.keyconnect.server.exchanges.services.StreamingOrderBookConsumer;
import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

@Deprecated
public class KrakenOrderBookConsumer extends StreamingOrderBookConsumer {

  public static final String NAME = "kraken";

  public KrakenOrderBookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, KrakenStreamingExchange.class);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
