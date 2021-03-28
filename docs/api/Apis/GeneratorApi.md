# GeneratorApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**generateXrpTransaction**](GeneratorApi.md#generateXrpTransaction) | **GET** /v1/blockchains/xrp/generator/payment | Generates a transaction as a payment.


<a name="generateXrpTransaction"></a>
# **generateXrpTransaction**
> Object generateXrpTransaction(sourceAccount, destinationAccount, amount, destinationTag, fee, network)

Generates a transaction as a payment.

    Helper method to generate a transaction object for the purpose of a payment between the source account identified by &#x60;sourceAccount&#x60; and destination account idenfied by &#x60;destinationAccount&#x60; for the value of &#x60;amount&#x60;.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **sourceAccount** | **String**| Source account | [default to null]
 **destinationAccount** | **String**| Destination account | [default to null]
 **amount** | **String**| Payment amount in drops | [default to null]
 **destinationTag** | **BigDecimal**| Destination tag | [optional] [default to null]
 **fee** | **String**| Fee to the ledger in drops. If this field is omitted, then &#x60;network&#x60; must be specified. | [optional] [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint. This field is required if &#x60;fee&#x60; is not specified in order to dynamically obtain the fee from the network.  | [optional] [default to mainnet]

### Return type

[**Object**](../Models/object.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

