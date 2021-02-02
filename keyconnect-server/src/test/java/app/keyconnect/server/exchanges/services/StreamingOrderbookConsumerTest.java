package app.keyconnect.server.exchanges.services;

import static org.assertj.core.api.Assertions.assertThat;

import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;

class StreamingOrderbookConsumerTest {

  @Test
  void consume() throws Exception {
    final OrderbookConsumer subject = new StreamingOrderbookConsumer(CurrencyPair.ETH_USD,
        CoinbaseProStreamingExchange.class);
    subject.start();
    Thread.sleep(5000);
    subject.stop();
    final OrderBook orderBook = subject.getOrderBook();
    assertThat(orderBook).isNotNull();

  }
}
