package app.keyconnect.server.exchanges.services;

import info.bitrich.xchangestream.bitfinex.BitfinexStreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

public class BitfinexOrderbookConsumer extends StreamingOrderbookConsumer {

  public BitfinexOrderbookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, BitfinexStreamingExchange.class);
  }
}
