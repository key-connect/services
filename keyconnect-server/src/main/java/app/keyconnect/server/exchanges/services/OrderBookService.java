package app.keyconnect.server.exchanges.services;

import org.knowm.xchange.service.marketdata.MarketDataService;

public class OrderBookService {

  private final MarketDataService marketDataService;

  public OrderBookService(MarketDataService marketDataService) {
    this.marketDataService = marketDataService;
  }

  // todo add data models for OrderBook (asks, bids etc) in the keyconnect-api yaml
  //   and implement methods here to return / work with that data model
}
