package app.keyconnect.server.controllers;

import app.keyconnect.api.client.model.MarketsItem;
import app.keyconnect.api.client.model.MarketsItem.StatusEnum;
import app.keyconnect.api.client.model.MarketsResponse;
import app.keyconnect.api.client.model.Order;
import app.keyconnect.api.client.model.OrderBook;
import app.keyconnect.server.exchanges.factories.MarketDataFactory;
import app.keyconnect.server.exchanges.services.OrderBookConsumer;
import app.keyconnect.server.exchanges.services.aggregators.OrderBookAggregator;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MarketsController {

  private final MarketDataFactory marketDataFactory;

  public MarketsController(
      MarketDataFactory marketDataFactory) {
    this.marketDataFactory = marketDataFactory;
  }

  @GetMapping(
      path = "/v1/markets/{base}/{counter}/orderbook",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<OrderBook> getAggregatedOrderbook(
      @PathVariable("base") String base,
      @PathVariable("counter") String counter
  ) {
    base = base.toUpperCase(Locale.ROOT);
    counter = counter.toUpperCase(Locale.ROOT);

    final CurrencyPair pair = new CurrencyPair(base,
        counter);
    final OrderBookAggregator aggregator = marketDataFactory
        .getAggregatorForCurrencyPair(pair);
    final List<LimitOrder> asks = aggregator.getAsks();
    final List<LimitOrder> bids = aggregator.getBids();
    return ResponseEntity.ok(
        new OrderBook()
            .base(base)
            .counter(counter)
            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
            .asks(
                asks.stream()
                    .map(this::toOrder)
                    .collect(Collectors.toList())
            )
            .bids(
                bids.stream()
                    .map(this::toOrder)
                    .collect(Collectors.toList())
            )
    );
  }

  @GetMapping(
      path = "/v1/markets",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<MarketsResponse> getMarkets() {
    return ResponseEntity.ok(
        new MarketsResponse()
            .markets(
                marketDataFactory.getExchangeServices()
                    .stream()
                    .map(e -> new MarketsItem()
                        .name(e.getName())
                        .status(e.isConnected() ? StatusEnum.CONNECTED : StatusEnum.DISCONNECTED)
                        .currencies(
                            e.getCurrencies()
                                .stream()
                                .map(CurrencyPair::toString)
                                .collect(Collectors.toList())
                        ))
                    .collect(Collectors.toList())
            )
    );
  }

  @GetMapping(
      path = "/v1/markets/{base}/{counter}/exchanges/{exchange}/orderbook",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<OrderBook> getAggregatedOrderbook(
      @PathVariable("base") String base,
      @PathVariable("counter") String counter,
      @PathVariable("exchange") String exchange
  ) {
    base = base.toUpperCase(Locale.ROOT);
    counter = counter.toUpperCase(Locale.ROOT);
    exchange = exchange.toLowerCase(Locale.ROOT);

    final CurrencyPair pair = new CurrencyPair(base,
        counter);
    final Optional<OrderBookConsumer> maybeConsumer = marketDataFactory
        .getConsumerForNameAndCurrencyPair(exchange, pair);

    if (maybeConsumer.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "exchange/curency pair combination was not found");
    }

    final OrderBookConsumer consumer = maybeConsumer.get();
    final List<LimitOrder> asks = consumer.getAsks();
    final List<LimitOrder> bids = consumer.getBids();
    return ResponseEntity.ok(
        new OrderBook()
            .base(base)
            .counter(counter)
            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
            .asks(
                asks.stream()
                    .map(this::toOrder)
                    .collect(Collectors.toList())
            )
            .bids(
                bids.stream()
                    .map(this::toOrder)
                    .collect(Collectors.toList())
            )
    );
  }

  private Order toOrder(LimitOrder o) {
    final Order order = new Order()
        .amount(o.getRemainingAmount())
        .price(o.getLimitPrice());
    if (StringUtils.isNotBlank(o.getId())) {
      order.id(o.getId());
    }

    if (o.getTimestamp() != null) {
      order.timestamp(String.valueOf(o.getTimestamp().toInstant().toEpochMilli()));
    }
    return order;
  }
}
