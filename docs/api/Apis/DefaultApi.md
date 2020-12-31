# DefaultApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**generateTransaction**](DefaultApi.md#generateTransaction) | **GET** /v1/blockchains/xrp/generator/payment | Generate a transaction
[**getAccountInfo**](DefaultApi.md#getAccountInfo) | **GET** /v1/blockchains/{chainId}/accounts/{accountId} | Returns account / wallet information for the provided chainId representing the blockchain.
[**getAccountPayments**](DefaultApi.md#getAccountPayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Gets payments for a given account on a given blockchain specified by the &#x60;chainId&#x60; parameter.
[**getAccountTransactions**](DefaultApi.md#getAccountTransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Gets transactions for given account on a given blockchain specified by the &#x60;chainId&#x60; parameter.
[**getBlockchainStatus**](DefaultApi.md#getBlockchainStatus) | **GET** /v1/blockchains/{chainId}/status | Returns status of the provided chainId.
[**getBlockchainsStatus**](DefaultApi.md#getBlockchainsStatus) | **GET** /v1/blockchains/status | Returns list of available blockchains and their statuses.
[**getFee**](DefaultApi.md#getFee) | **GET** /v1/blockchains/{chainId}/fee | Returns fee of the provided chainId.
[**getServerStatus**](DefaultApi.md#getServerStatus) | **GET** /v1/server/status | Returns status of the server.
[**getTransaction**](DefaultApi.md#getTransaction) | **GET** /v1/blockchains/{chainId}/transactions/{hash} | Get a single transaction by its hash on the specified &#x60;chainId&#x60;.
[**submitTransaction**](DefaultApi.md#submitTransaction) | **POST** /v1/blockchains/{chainId}/transactions | Submit a transaction to the blockchain


<a name="generateTransaction"></a>
# **generateTransaction**
> Object generateTransaction(sourceAccount, destinationAccount, amount, destinationTag, fee, network)

Generate a transaction

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

<a name="getAccountInfo"></a>
# **getAccountInfo**
> BlockchainAccountInfo getAccountInfo(chainId, accountId, network)

Returns account / wallet information for the provided chainId representing the blockchain.

    Used to get walletInfo for a given blockchain, i.e. for chainId&#x3D;xrp this endpoint will return info regarding a provided accountId.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**| ID of the block chain | [default to null] [enum: xrp, eth, btc]
 **accountId** | **String**| Identifier representing the account. This is most likely the wallet address. | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]

### Return type

[**BlockchainAccountInfo**](../Models/BlockchainAccountInfo.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getAccountPayments"></a>
# **getAccountPayments**
> BlockchainAccountPayments getAccountPayments(chainId, accountId, network, cursor)

Gets payments for a given account on a given blockchain specified by the &#x60;chainId&#x60; parameter.

    Returns paginated list of payments.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**|  | [default to null] [enum: xrp, eth, btc]
 **accountId** | **String**| Identifier representing the account. This is most likely the wallet address. | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]
 **cursor** | **String**| Cursor representing position among pages. Pass the cursor from previous get payments response to get the next page. | [optional] [default to null]

### Return type

[**BlockchainAccountPayments**](../Models/BlockchainAccountPayments.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getAccountTransactions"></a>
# **getAccountTransactions**
> BlockchainAccountTransactions getAccountTransactions(chainId, accountId, network, cursor)

Gets transactions for given account on a given blockchain specified by the &#x60;chainId&#x60; parameter.

    Returns paginated list of transactions.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**|  | [default to null] [enum: xrp, eth, btc]
 **accountId** | **String**| Identifier representing the account. This is most likely the wallet address. | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]
 **cursor** | **String**| Cursor representing position among pages. Pass the cursor from previous get transactions response to get the next page. | [optional] [default to null]

### Return type

[**BlockchainAccountTransactions**](../Models/BlockchainAccountTransactions.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getBlockchainStatus"></a>
# **getBlockchainStatus**
> BlockchainStatus getBlockchainStatus(chainId, network)

Returns status of the provided chainId.

    Optional extended description in Markdown.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**| ID of the block chain | [default to null] [enum: xrp, eth, btc]
 **network** | **String**| Blockchain network to get the status from. | [optional] [default to null]

### Return type

[**BlockchainStatus**](../Models/BlockchainStatus.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getBlockchainsStatus"></a>
# **getBlockchainsStatus**
> AvailableBlockchains getBlockchainsStatus()

Returns list of available blockchains and their statuses.

### Parameters
This endpoint does not need any parameter.

### Return type

[**AvailableBlockchains**](../Models/AvailableBlockchains.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getFee"></a>
# **getFee**
> BlockchainFee getFee(chainId, network)

Returns fee of the provided chainId.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**| ID of the block chain | [default to null] [enum: xrp, eth, btc]
 **network** | **String**| Blockchain network to get the status from. | [optional] [default to null]

### Return type

[**BlockchainFee**](../Models/BlockchainFee.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getServerStatus"></a>
# **getServerStatus**
> ServerStatusResponse getServerStatus()

Returns status of the server.

    Returns server status along with any error information (if any).

### Parameters
This endpoint does not need any parameter.

### Return type

[**ServerStatusResponse**](../Models/ServerStatusResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getTransaction"></a>
# **getTransaction**
> BlockchainAccountTransaction getTransaction(chainId, hash, network)

Get a single transaction by its hash on the specified &#x60;chainId&#x60;.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**|  | [default to null] [enum: xrp, eth, btc]
 **hash** | **String**| Hash of the transaction to lookup | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]

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

Submit a transaction to the blockchain

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**|  | [default to null] [enum: xrp, eth, btc]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]
 **SubmitTransactionRequest** | [**SubmitTransactionRequest**](../Models/SubmitTransactionRequest.md)| Request payload containing the transaction to submit to specified &#x60;chainId&#x60;. | [optional]

### Return type

[**SubmitTransactionResult**](../Models/SubmitTransactionResult.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

