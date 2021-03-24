package app.keyconnect.server.exchanges.services;

import info.bitrich.xchangestream.core.StreamingExchange;
import org.knowm.xchange.currency.CurrencyPair;

public class ExchangeOrderBookConsumer extends StreamingOrderBookConsumer {

  private final String name;

  public ExchangeOrderBookConsumer(String name, Class<? extends StreamingExchange> exchangeClass, CurrencyPair currencyPair) {
    super(currencyPair, exchangeClass);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
