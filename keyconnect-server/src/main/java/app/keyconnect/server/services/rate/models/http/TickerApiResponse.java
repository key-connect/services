package app.keyconnect.server.services.rate.models.http;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TickerApiResponse {

  private Ticker ticker;
  private long timestamp;
  private boolean success;
  private String error;

}
