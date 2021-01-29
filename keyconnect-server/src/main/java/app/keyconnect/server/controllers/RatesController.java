package app.keyconnect.server.controllers;

import app.keyconnect.api.client.model.RateResponse;
import app.keyconnect.server.services.rate.RateService;
import app.keyconnect.server.services.rate.models.Rate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class RatesController {

  private final RateService rateService;

  @Autowired
  public RatesController(RateService rateService) {
    this.rateService = rateService;
  }

  @GetMapping(
      path = "/v1/rates/{base}/{counter}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<RateResponse> getRate(
      @PathVariable("base") String base,
      @PathVariable("counter") String counter
  ) {
    if (StringUtils.isAnyBlank(base, counter)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base and counter must not be blank");
    }

    final Rate rate = rateService.getRate(base, counter);

    if (rate == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "Rate was not found between base " + base + " and counter " + counter);
    }

    return ResponseEntity.ok(
        new RateResponse()
            .avg(
                new app.keyconnect.api.client.model.Rate()
                    .base(base)
                    .counter(counter)
                    .price(rate.getPrice().toString())
            )
    );
  }
}
