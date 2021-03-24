package app.keyconnect.server.exchanges.services;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.server.exchanges.services.consumers.BinanceOrderBookConsumer;
import java.math.BigDecimal;
import org.knowm.xchange.currency.CurrencyPair;

public class BinanceOrderBookConsumerTest {

//  @Test
  public void binanceOrderBook() throws Exception {
    final OrderBookConsumer consumer = new BinanceOrderBookConsumer(CurrencyPair.ETH_BTC);
    consumer.start();
    Thread.sleep(10 * 1000);
    consumer.stop();

    final BigDecimal askVolume = consumer.getAskVolume();
    final BigDecimal bidVolume = consumer.getBidVolume();

    assertThat(askVolume).isGreaterThan(BigDecimal.ZERO);
    assertThat(bidVolume).isGreaterThan(BigDecimal.ZERO);
  }
}
