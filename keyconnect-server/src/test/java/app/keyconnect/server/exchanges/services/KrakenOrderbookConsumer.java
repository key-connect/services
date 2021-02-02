package app.keyconnect.server.exchanges.services;

import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

public class KrakenOrderbookConsumer extends StreamingOrderbookConsumer {

  public KrakenOrderbookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, KrakenStreamingExchange.class);
  }
}
