package app.keyconnect.server.exchanges.services;

import io.reactivex.Observer;
import java.math.BigDecimal;
import java.util.List;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.trade.LimitOrder;

public interface OrderBookConsumer {

  void start();

  void stop();

  void subscribe(Observer<OrderBook> observable);

  List<LimitOrder> getAsks();

  List<LimitOrder> getBids();

  BigDecimal getAskVolume();

  BigDecimal getBidVolume();

  CurrencyPair getCurrencyPair();

  String getName();

}
