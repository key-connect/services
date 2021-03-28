package app.keyconnect.server.exchanges.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Test;
import org.knowm.xchange.currency.CurrencyPair;

public class ExchangeNameServiceTest {

  @Test
  public void returnsSupportedCurrencyPairs() {
    for (String name : ExchangeNameService.getKnownExchanges()) {
      final List<CurrencyPair> pairs = ExchangeNameService.supportedCurrencyPairs(name);
      assertThat(pairs).describedAs(name + " returned >0 pairs").hasSizeGreaterThan(0);
    }
  }
}
