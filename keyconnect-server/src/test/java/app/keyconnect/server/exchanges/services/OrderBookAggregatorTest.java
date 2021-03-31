package app.keyconnect.server.exchanges.services;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.server.exchanges.ExchangeService;
import app.keyconnect.server.exchanges.services.aggregators.OrderBookAggregator;
import app.keyconnect.server.exchanges.services.consumers.BitstampOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.CoinbaseProOrderBookConsumer;
import app.keyconnect.server.exchanges.services.consumers.KrakenOrderBookConsumer;
import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import info.bitrich.xchangestream.coinbasepro.CoinbaseProStreamingExchange;
import info.bitrich.xchangestream.kraken.KrakenStreamingExchange;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.kraken.dto.marketdata.KrakenOHLCs;

public class OrderBookAggregatorTest {

  @Test
  public void consumeMultipleOrderBooks() throws Exception {
    final CurrencyPair pair = CurrencyPair.ETH_USD;
    final ExchangeService bsExchangeService = new ExchangeService(
        BitstampOrderBookConsumer.NAME, BitstampStreamingExchange.class);
    final ExchangeOrderBookConsumer bsConsumer = new ExchangeOrderBookConsumer("bitstamp",
        bsExchangeService, pair);
    final ExchangeService krakenExchangeService = new ExchangeService(
        KrakenOrderBookConsumer.NAME, KrakenStreamingExchange.class);
    final ExchangeOrderBookConsumer krakenConsumer = new ExchangeOrderBookConsumer("kraken",
        krakenExchangeService, pair);
    final ExchangeService cbpExchangeService = new ExchangeService(
        CoinbaseProOrderBookConsumer.NAME, CoinbaseProStreamingExchange.class);
    final ExchangeOrderBookConsumer cbpConsumer = new ExchangeOrderBookConsumer("cbp",
        cbpExchangeService, pair);
    final OrderBookAggregator subject = new OrderBookAggregator(
        bsConsumer,
        krakenConsumer,
        cbpConsumer
    );
    bsExchangeService.connect();
    krakenExchangeService.connect();
    cbpExchangeService.connect();
    Thread.sleep(5000);
    assertThat(subject.getAsks()).hasSizeGreaterThan(0);
    assertThat(subject.getBids()).hasSizeGreaterThan(0);
  }
}
