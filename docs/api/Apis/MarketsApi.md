# MarketsApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAggregatedOrderBook**](MarketsApi.md#getAggregatedOrderBook) | **GET** /v1/markets/{base}/{counter}/orderbook | Get aggregated order book for the provided base and counter
[**getAvailableMarkets**](MarketsApi.md#getAvailableMarkets) | **GET** /v1/markets | Get available markets
[**getExchangeOrderBook**](MarketsApi.md#getExchangeOrderBook) | **GET** /v1/markets/{base}/{counter}/exchanges/{exchange}/orderbook | Get order book for the provided base and counter at specified exchange


<a name="getAggregatedOrderBook"></a>
# **getAggregatedOrderBook**
> OrderBook getAggregatedOrderBook(base, counter)

Get aggregated order book for the provided base and counter

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **base** | **String**| Base currency | [default to null]
 **counter** | **String**| Counter currency | [default to null]

### Return type

[**OrderBook**](../Models/OrderBook.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getAvailableMarkets"></a>
# **getAvailableMarkets**
> MarketsResponse getAvailableMarkets()

Get available markets

### Parameters
This endpoint does not need any parameter.

### Return type

[**MarketsResponse**](../Models/MarketsResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getExchangeOrderBook"></a>
# **getExchangeOrderBook**
> OrderBook getExchangeOrderBook(base, counter, exchange)

Get order book for the provided base and counter at specified exchange

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **base** | **String**| Base currency | [default to null]
 **counter** | **String**| Counter currency | [default to null]
 **exchange** | **String**| Exchange name | [default to null]

### Return type

[**OrderBook**](../Models/OrderBook.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

