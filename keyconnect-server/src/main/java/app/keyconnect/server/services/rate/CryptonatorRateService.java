package app.keyconnect.server.services.rate;

import app.keyconnect.server.services.rate.models.Rate;
import app.keyconnect.server.services.rate.models.http.Ticker;
import app.keyconnect.server.services.rate.models.http.TickerApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;

public class CryptonatorRateService implements RateService {

  private static final String BASE_PATH = "https://api.cryptonator.com/api/ticker";
  private static final Logger logger = LoggerFactory.getLogger(CryptonatorRateService.class);
  private final RestTemplate restTemplate;

  public CryptonatorRateService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  @Cacheable(value = "slow", unless = "#result == null")
  public Rate getRate(String base, String counter) {
    final TickerApiResponse apiResponse = getTicker(base, counter);
    if (apiResponse.getTicker() != null) {
      final Ticker ticker = apiResponse.getTicker();
      return new Rate(base, counter, ticker.getPrice());
    }
    return null;
  }

  private TickerApiResponse getTicker(String base, String counter) {
    final TickerApiResponse apiResponse = restTemplate
        .getForObject(String.format("%s/%s-%s", BASE_PATH, base, counter), TickerApiResponse.class);
    if (!apiResponse.isSuccess()) {
      logger.error("Failed to get ticker base={}, counter={}, message={}", base, counter, apiResponse.getError());
    }
    return apiResponse;
  }
}
