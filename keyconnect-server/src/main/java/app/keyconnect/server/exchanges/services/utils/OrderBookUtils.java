package app.keyconnect.server.exchanges.services.utils;

import app.keyconnect.api.client.model.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderBookUtils {

  private static final Logger logger = LoggerFactory.getLogger(OrderBookUtils.class);

  public static void indexOrders(Map<BigDecimal, Order> priceMap,
      List<LimitOrder> limitOrders) {
    for (LimitOrder order : limitOrders) {
      final BigDecimal price = order.getLimitPrice();
      if (!priceMap.containsKey(price)) {
        priceMap.put(price, toOrder(order));
        continue;
      }

      final Order currentValue = priceMap.get(price);
      if (order.getTimestamp() != null
          && currentValue.getTimestamp() != null
          && order.getTimestamp().toInstant()
          .isBefore(Instant.ofEpochMilli(Long.parseLong(currentValue.getTimestamp())))
      ) {
        // we have a newer value in our map, skip
        continue;
      }

      if (order.getRemainingAmount() == null || order.getRemainingAmount().equals(BigDecimal.ZERO)) {
        priceMap.remove(price);
        logger.info("Removing consumed price {}", price);
        continue;
      }

      priceMap.replace(price, currentValue, toOrder(order));
    }
  }

  private static Order toOrder(LimitOrder order) {
    final String timestamp;
    if (order.getTimestamp() != null) {
      timestamp = String.valueOf(order.getTimestamp().toInstant().toEpochMilli());
    } else {
      timestamp = null;
    }

    return new Order()
        .id(order.getId())
        .price(order.getLimitPrice())
        .timestamp(timestamp)
        .amount(order.getRemainingAmount());
  }
}
