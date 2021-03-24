package app.keyconnect.server.exchanges.services;

import static org.assertj.core.api.Assertions.assertThat;

import info.bitrich.xchangestream.bitstamp.v2.BitstampStreamingExchange;
import java.math.BigDecimal;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StreamingOrderBookConsumerTest {

  private static final Logger logger = LoggerFactory.getLogger(StreamingOrderBookConsumerTest.class);

  @Test
  void consume() throws Exception {
    final OrderBookConsumer subject = new StreamingOrderBookConsumer(CurrencyPair.ETH_USD, BitstampStreamingExchange.class) {
      @Override
      public String getName() {
        return "testexchange";
      }
    };
    subject.start();
    Thread.sleep(5 * 1000);
    subject.stop();
    final List<LimitOrder> asks = subject.getAsks();
    final List<LimitOrder> bids = subject.getBids();
    assertThat(asks).isNotNull();
    assertThat(bids).isNotNull();
    final BigDecimal askVolume = subject.getAskVolume();
    logger.info("ask volume={}", askVolume);
    final BigDecimal bidVolume = subject.getBidVolume();
    logger.info("bid volume={}", bidVolume);

    assertThat(askVolume).isGreaterThan(BigDecimal.ZERO);
    assertThat(bidVolume).isGreaterThan(BigDecimal.ZERO);
  }
}
