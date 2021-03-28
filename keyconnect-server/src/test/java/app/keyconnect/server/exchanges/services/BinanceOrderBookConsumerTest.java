package app.keyconnect.server.exchanges.services;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.server.exchanges.ExchangeService;
import app.keyconnect.server.exchanges.services.consumers.BinanceOrderBookConsumer;
import info.bitrich.xchangestream.binance.BinanceStreamingExchange;
import java.math.BigDecimal;
import org.knowm.xchange.currency.CurrencyPair;

public class BinanceOrderBookConsumerTest {

//  @Test
  public void binanceOrderBook() throws Exception {
    final ExchangeService exchangeService = new ExchangeService(BinanceOrderBookConsumer.NAME, BinanceStreamingExchange.class);
    final OrderBookConsumer consumer = new BinanceOrderBookConsumer(exchangeService, CurrencyPair.ETH_BTC);
    exchangeService.connect();
    Thread.sleep(10 * 1000);
    exchangeService.disconnect();

    final BigDecimal askVolume = consumer.getAskVolume();
    final BigDecimal bidVolume = consumer.getBidVolume();

    assertThat(askVolume).isGreaterThan(BigDecimal.ZERO);
    assertThat(bidVolume).isGreaterThan(BigDecimal.ZERO);
  }
}
