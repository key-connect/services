package app.keyconnect.server.services.rate;

import app.keyconnect.server.services.rate.models.Rate;

public interface RateService {

  Rate getRate(String base, String counter);

}
