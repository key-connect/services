package app.keyconnect.server.exchanges.services;

import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

public class BitstampOrderbookConsumer extends StreamingOrderbookConsumer {

  public BitstampOrderbookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, BitstampStreamingExchange.class);
  }
}
