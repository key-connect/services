package app.keyconnect.server.services.rate.models;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RateEval {

  private Rate rate;
  private BigDecimal amount;
  private final List<Rate> path = new LinkedList<>();

}
