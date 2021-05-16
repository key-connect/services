package app.keyconnect.server.controllers;

import app.keyconnect.api.client.model.MarketsItem;
import app.keyconnect.api.client.model.MarketsItem.StatusEnum;
import app.keyconnect.api.client.model.MarketsResponse;
import app.keyconnect.api.client.model.Order;
import app.keyconnect.api.client.model.OrderBook;
import app.keyconnect.server.exchanges.ExchangeService;
import app.keyconnect.server.exchanges.factories.MarketDataFactory;
import app.keyconnect.server.exchanges.services.OrderBookConsumer;
import app.keyconnect.server.exchanges.services.aggregators.OrderBookAggregator;
import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnProperty(value = "api.markets.enabled", havingValue = "true")
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
    final Optional<OrderBookAggregator> maybeAggregator = marketDataFactory
        .getAggregatorForCurrencyPair(pair);
    if (maybeAggregator.isEmpty()) {
      return ResponseEntity.ok(
          new OrderBook()
              .base(base)
              .counter(counter)
              .exchanges(Collections.emptyList())
      );
    }
    final OrderBookAggregator aggregator = maybeAggregator.get();
    final List<Order> asks = aggregator.getAsks();
    asks.sort(Comparator.comparing(Order::getPrice));
    final List<Order> bids = aggregator.getBids();
    bids.sort(Comparator.comparing(Order::getPrice));
    final List<String> aggregatedExchanges = aggregator.getExchangeServices().stream()
        .map(ExchangeService::getName)
        .collect(Collectors.toList());
    return ResponseEntity.ok(
        new OrderBook()
            .base(base)
            .counter(counter)
            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
            .asks(asks)
            .exchanges(aggregatedExchanges)
            .bids(bids)
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
      return ResponseEntity.ok(
          new OrderBook()
              .base(base)
              .counter(counter)
              .exchanges(Collections.emptyList())
      );
    }

    final OrderBookConsumer consumer = maybeConsumer.get();
    final List<Order> asks = consumer.getAsks();
    asks.sort(Comparator.comparing(Order::getPrice));
    final List<Order> bids = consumer.getBids();
    bids.sort(Comparator.comparing(Order::getPrice));

    return ResponseEntity.ok(
        new OrderBook()
            .base(base)
            .counter(counter)
            .timestamp(String.valueOf(Instant.now().toEpochMilli()))
            .exchanges(Collections.singletonList(consumer.getName()))
            .asks(asks)
            .bids(bids)
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
