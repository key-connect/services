package app.keyconnect.server.exchanges.services;

import app.keyconnect.api.client.model.Order;
import app.keyconnect.server.exchanges.ExchangeService;
import java.math.BigDecimal;
import java.util.List;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;

public interface OrderBookConsumer {

  ExchangeService getExchangeService();

  List<Order> getAsks();

  List<Order> getBids();

  BigDecimal getAskVolume();

  BigDecimal getBidVolume();

  CurrencyPair getCurrencyPair();

  String getName();

}
