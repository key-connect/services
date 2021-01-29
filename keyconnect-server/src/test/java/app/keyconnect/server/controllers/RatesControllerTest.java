package app.keyconnect.server.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import app.keyconnect.api.client.model.RateResponse;
import app.keyconnect.server.services.rate.RateService;
import app.keyconnect.server.services.rate.models.Rate;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

class RatesControllerTest {

  private RatesController subject;
  private RateService mockRateService;

  @BeforeEach
  void beforeAll() {
    mockRateService = mock(RateService.class);
    subject = new RatesController(mockRateService);
  }

  @Test
  void throwsBadRequestWhenBaseIsEmpty() {
    try {
      subject.getRate("", "USD");
      fail("should've thrown bad request exception");
    } catch (ResponseStatusException e) {
      assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void throwsBadRequestWhenCounterIsEmpty() {
    try {
      subject.getRate("ETH", "");
      fail("should've thrown bad request exception");
    } catch (ResponseStatusException e) {
      assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void throwsNotFoundWhenReturnedRateIsNull() {
    final String base = "ETH";
    final String counter = "USD";
    doReturn(null).when(mockRateService).getRate(eq(base), eq(counter));
    try {
      subject.getRate(base, counter);
      fail("should've thrown not found exception");
    } catch (ResponseStatusException e) {
      assertThat(e.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
  }

  @Test
  void returnsRateProvidedByRateService() {
    final String base = "eth";
    final String counter = "usd";
    final String price = "100000.87821894";
    final Rate serviceRate = new Rate(base, counter, new BigDecimal(price));
    doReturn(serviceRate).when(mockRateService).getRate(eq(base), eq(counter));

    final ResponseEntity<RateResponse> response = subject.getRate(base, counter);
    assertThat(response.getStatusCodeValue()).isEqualTo(200);

    final RateResponse rateResponse = response.getBody();
    assertThat(rateResponse).isNotNull();
    assertThat(rateResponse.getAvg()).isNotNull();
    assertThat(rateResponse.getAvg().getBase()).isEqualTo(base);
    assertThat(rateResponse.getAvg().getCounter()).isEqualTo(counter);
    assertThat(rateResponse.getAvg().getPrice()).isEqualTo(price);
  }
}
