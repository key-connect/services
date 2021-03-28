# TransactionsApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getAccountTransactions**](TransactionsApi.md#getAccountTransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Returns paginated list of transactions.
[**getTransaction**](TransactionsApi.md#getTransaction) | **GET** /v1/blockchains/{chainId}/transactions/{hash} | Get a single transaction object by its provided &#x60;hash&#x60; on the specified &#x60;chainId&#x60;.
[**submitTransaction**](TransactionsApi.md#submitTransaction) | **POST** /v1/blockchains/{chainId}/transactions | Submit a transaction to the blockchain.


<a name="getAccountTransactions"></a>
# **getAccountTransactions**
> BlockchainAccountTransactions getAccountTransactions(chainId, accountId, network, cursor, fiat)

Returns paginated list of transactions.

    Gets transactions for given &#x60;accountId&#x60; on a given blockchain identified by the &#x60;chainId&#x60; parameter. Return the &#x60;cursor&#x60; value in the next call to get the next page.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**|  | [default to null] [enum: xrp, eth]
 **accountId** | **String**| Identifier representing the account. This is most likely the wallet address. | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]
 **cursor** | **String**| Cursor representing position among pages. Pass the cursor from previous get transactions response to get the next page. | [optional] [default to null]
 **fiat** | **String**| Used to provide equivalent value in specified fiat | [optional] [default to null] [enum: USD, GBP, EUR]

### Return type

[**BlockchainAccountTransactions**](../Models/BlockchainAccountTransactions.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getTransaction"></a>
# **getTransaction**
> BlockchainAccountTransaction getTransaction(chainId, hash, network, fiat)

Get a single transaction object by its provided &#x60;hash&#x60; on the specified &#x60;chainId&#x60;.

    Returned transaction object might be in greater detail than the one provided in the list.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**|  | [default to null] [enum: xrp, eth]
 **hash** | **String**| Hash of the transaction to lookup | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]
 **fiat** | **String**| Used to provide equivalent value in specified fiat | [optional] [default to null] [enum: USD, GBP, EUR]

### Return type

[**BlockchainAccountTransaction**](../Models/BlockchainAccountTransaction.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="submitTransaction"></a>
# **submitTransaction**
> SubmitTransactionResult submitTransaction(chainId, network, SubmitTransactionRequest)

Submit a transaction to the blockchain.

    Provided transaction object is submitted directly to the blockchain identified by &#x60;chainId&#x60; as is.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**|  | [default to null] [enum: xrp, eth]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]
 **SubmitTransactionRequest** | [**SubmitTransactionRequest**](../Models/SubmitTransactionRequest.md)| Request payload containing the transaction to submit to specified &#x60;chainId&#x60;. | [optional]

### Return type

[**SubmitTransactionResult**](../Models/SubmitTransactionResult.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

