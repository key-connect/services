# RatesApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getRate**](RatesApi.md#getRate) | **GET** /v1/rates/{base}/{counter} | Gets the currency conversion rate for the provided base and counter currency.


<a name="getRate"></a>
# **getRate**
> RateResponse getRate(base, counter)

Gets the currency conversion rate for the provided base and counter currency.

    Returns the applicable rate to convert between the provided base and counter currency.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **base** | **String**| Base currency | [default to null]
 **counter** | **String**| Counter currency | [default to null]

### Return type

[**RateResponse**](../Models/RateResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

