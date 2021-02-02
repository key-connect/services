package app.keyconnect.server.exchanges.services;

import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

public class CoinbaseProOrderbookConsumer extends StreamingOrderbookConsumer {

  public CoinbaseProOrderbookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, CoinbaseProStreamingExchange.class);
  }
}
