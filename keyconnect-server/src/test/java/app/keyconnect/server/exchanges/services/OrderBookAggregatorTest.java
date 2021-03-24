package app.keyconnect.server.exchanges.services;

import app.keyconnect.server.exchanges.services.aggregators.OrderBookAggregator;
import app.keyconnect.server.exchanges.services.consumers.BitstampOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.CoinbaseProOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.KrakenOrderBookConsumer;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;

public class OrderBookAggregatorTest {

  @Test
  public void consumeMultipleOrderBooks() throws Exception {
    final CurrencyPair pair = CurrencyPair.ETH_USD;
    final BitstampOrderBookConsumer bsConsumer = new BitstampOrderBookConsumer(pair);
    final KrakenOrderBookConsumer krakenConsumer = new KrakenOrderBookConsumer(pair);
    final CoinbaseProOrderBookConsumer cbpConsumer = new CoinbaseProOrderBookConsumer(pair);
    final OrderBookAggregator subject = new OrderBookAggregator(
        bsConsumer,
        krakenConsumer,
        cbpConsumer
    );
    subject.start();
    Thread.sleep(5000);
    subject.stop();
  }
}
