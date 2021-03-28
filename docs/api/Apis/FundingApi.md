# FundingApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**fundAccount**](FundingApi.md#fundAccount) | **POST** /v1/blockchains/{chainId}/accounts/{accountId}/fund | Funds the given account if funding is available for the specified network.


<a name="fundAccount"></a>
# **fundAccount**
> fundAccount(chainId, accountId, network)

Funds the given account if funding is available for the specified network.

    Uses the blockchain funding APIs to fund a given account.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**| ID of the block chain | [default to null] [enum: xrp, eth]
 **accountId** | **String**| Identifier representing the account. This is most likely the wallet address. | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

