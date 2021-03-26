package app.keyconnect.server.exchanges.services;

import app.keyconnect.server.exchanges.ExchangeService;
import org.knowm.xchange.currency.CurrencyPair;

public class ExchangeOrderBookConsumer extends StreamingOrderBookConsumer {

  private final String name;

  public ExchangeOrderBookConsumer(String name, ExchangeService exchangeService,
      CurrencyPair currencyPair) {
    super(exchangeService, currencyPair);
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
