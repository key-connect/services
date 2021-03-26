package app.keyconnect.server.exchanges.services.consumers;

import app.keyconnect.server.exchanges.ExchangeService;
import app.keyconnect.server.exchanges.services.StreamingOrderBookConsumer;
import org.knowm.xchange.currency.CurrencyPair;

/**
 * BROKEN DO NOT USE, run BinanceOrderBookConsumerTest for details on failure
 */
public class BinanceOrderBookConsumer extends StreamingOrderBookConsumer {

  public static final String NAME = "binance";

  public BinanceOrderBookConsumer(ExchangeService exchangeService, CurrencyPair currencyPair) {
    super(exchangeService, currencyPair);
  }

  @Override
  public String getName() {
    return NAME;
  }
}
