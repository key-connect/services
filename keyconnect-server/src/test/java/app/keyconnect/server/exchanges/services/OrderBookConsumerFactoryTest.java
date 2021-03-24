package app.keyconnect.server.exchanges.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;

public class OrderBookConsumerFactoryTest {

  @Test
  public void suppliesConsumerForKnownExchanges() {
    final OrderBookConsumerFactory subject = new OrderBookConsumerFactory();
    final String[] knownExchanges = ExchangeNameService.getKnownExchanges();
    assertThat(knownExchanges).hasSize(4);

    final CurrencyPair pair = CurrencyPair.ETH_BTC;
    final List<OrderBookConsumer> consumers = Arrays.stream(knownExchanges)
        .map(e -> subject.consumer(e, pair))
        .collect(Collectors.toList());
    assertThat(consumers).hasSize(4);

    final List<OrderBookConsumer> consumersRepeat = Arrays.stream(knownExchanges)
        .map(e -> subject.consumer(e, pair))
        .collect(Collectors.toList());
    assertThat(consumers).hasSameElementsAs(consumersRepeat);
  }
}
