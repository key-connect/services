package app.keyconnect.server.exchanges.services.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBookUtils {

  private static final Logger logger = LoggerFactory.getLogger(OrderBookUtils.class);

  public static void indexOrders(Map<BigDecimal, LimitOrder> priceMap,
      List<LimitOrder> limitOrders) {
    for (LimitOrder order : limitOrders) {
      final BigDecimal price = order.getLimitPrice();
      if (!priceMap.containsKey(price)) {
        priceMap.put(price, order);
        continue;
      }

      final LimitOrder currentValue = priceMap.get(price);
      if (order.getTimestamp() != null
          && currentValue.getTimestamp() != null
          && order.getTimestamp().before(currentValue.getTimestamp())
      ) {
        // we have a newer value in our map, skip
        continue;
      }

      if (order.getRemainingAmount().equals(BigDecimal.ZERO)) {
        priceMap.remove(price);
        logger.info("Removing consumed price {}", price);
        continue;
      }

      priceMap.replace(price, currentValue, order);
    }
  }

}
