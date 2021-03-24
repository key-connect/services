package app.keyconnect.server.exchanges.services.consumers;

import app.keyconnect.server.exchanges.services.StreamingOrderBookConsumer;
import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import org.jetbrains.annotations.NotNull;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.CurrencyPair;

/**
 * BROKEN DO NOT USE, run BinanceOrderBookConsumerTest for details on failure
 */
public class BinanceOrderBookConsumer extends StreamingOrderBookConsumer {

  public static final String NAME = "binance";

  public BinanceOrderBookConsumer(CurrencyPair currencyPair) {
    super(currencyPair, BinanceStreamingExchange.class);
    setSpecification(getSpec());
  }

  @NotNull
  private ExchangeSpecification getSpec() {
    final ExchangeSpecification spec = new BinanceStreamingExchange().getDefaultExchangeSpecification();
//    spec.setApiKey("key");
//    spec.setSecretKey("secret");
    return spec;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
