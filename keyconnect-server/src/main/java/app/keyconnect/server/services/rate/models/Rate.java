package app.keyconnect.server.services.rate.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Rate {

  private String base;
  private String counter;
  private BigDecimal price;

  public Rate reverse() {
    return new Rate(counter, base, BigDecimal.ONE.divide(price, RoundingMode.HALF_UP));
  }
}
