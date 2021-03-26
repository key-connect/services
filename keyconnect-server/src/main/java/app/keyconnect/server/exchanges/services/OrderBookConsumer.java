package app.keyconnect.server.exchanges.services;

import app.keyconnect.server.exchanges.ExchangeService;
import java.math.BigDecimal;
import java.util.List;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;

public interface OrderBookConsumer {

  ExchangeService getExchangeService();

  List<LimitOrder> getAsks();

  List<LimitOrder> getBids();

  BigDecimal getAskVolume();

  BigDecimal getBidVolume();

  CurrencyPair getCurrencyPair();

  String getName();

}
