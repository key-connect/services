package app.keyconnect.server.services.rate;

import static org.assertj.core.api.Assertions.assertThat;

import app.keyconnect.server.services.rate.models.Rate;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

class CryptonatorRateServiceTest {

  @Test
  void getBtcRate() {
    final Rate rate = new CryptonatorRateService(new RestTemplate()).getRate("BTC", "USD");
    assertThat(rate).isNotNull();
    assertThat(rate.getPrice()).isGreaterThan(BigDecimal.ONE);
  }
}
