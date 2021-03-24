package app.keyconnect.server.exchanges.services.consumers;

import app.keyconnect.server.exchanges.services.StreamingOrderBookConsumer;
import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

public class BitfinexOrderBookConsumer extends StreamingOrderBookConsumer {

  public static final String NAME = "bitfinex";

  public BitfinexOrderBookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, BitfinexStreamingExchange.class);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
