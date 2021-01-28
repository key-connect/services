package app.keyconnect.server.services.rate.models.http;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Ticker {

  private String base;
  private String target;
  private BigDecimal price;
  private BigDecimal volume;
  private BigDecimal change;

}
