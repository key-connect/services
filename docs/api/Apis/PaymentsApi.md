# PaymentsApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAccountPayments**](PaymentsApi.md#getAccountPayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.


<a name="getAccountPayments"></a>
# **getAccountPayments**
> BlockchainAccountPayments getAccountPayments(chainId, accountId, network, cursor, fiat)

Returns paginated list of payments.

    Gets payments for given &#x60;accountId&#x60; on a given blockchain identified by the &#x60;chainId&#x60; parameter. Return the &#x60;cursor&#x60; value in the next call to get the next page.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**|  | [default to null] [enum: xrp, eth]
 **accountId** | **String**| Identifier representing the account. This is most likely the wallet address. | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]
 **cursor** | **String**| Cursor representing position among pages. Pass the cursor from previous get payments response to get the next page. | [optional] [default to null]
 **fiat** | **String**| Used to provide equivalent value in specified fiat | [optional] [default to null] [enum: USD, GBP, EUR]

### Return type

[**BlockchainAccountPayments**](../Models/BlockchainAccountPayments.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

