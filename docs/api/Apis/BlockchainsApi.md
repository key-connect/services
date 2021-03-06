# BlockchainsApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**fundAccount**](BlockchainsApi.md#fundAccount) | **POST** /v1/blockchains/{chainId}/accounts/{accountId}/fund | Funds the given account if funding is available for the specified network.
[**generateXrpTransaction**](BlockchainsApi.md#generateXrpTransaction) | **GET** /v1/blockchains/xrp/generator/payment | Generates a transaction as a payment.
[**getAccountInfo**](BlockchainsApi.md#getAccountInfo) | **GET** /v1/blockchains/{chainId}/accounts/{accountId} | Returns account / wallet information for the provided &#x60;chainId&#x60; representing the blockchain.
[**getAccountPayments**](BlockchainsApi.md#getAccountPayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.
[**getAccountTransactions**](BlockchainsApi.md#getAccountTransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Returns paginated list of transactions.
[**getBlockchainStatus**](BlockchainsApi.md#getBlockchainStatus) | **GET** /v1/blockchains/{chainId}/status | Returns the status of the provided blockchain.
[**getBlockchainsAccounts**](BlockchainsApi.md#getBlockchainsAccounts) | **POST** /v1/batch/blockchains/accounts/info | Gets account details of all specified accounts
[**getBlockchainsStatus**](BlockchainsApi.md#getBlockchainsStatus) | **GET** /v1/blockchains/status | Gets a list of all supported blockchains and their statuses.
[**getFee**](BlockchainsApi.md#getFee) | **GET** /v1/blockchains/{chainId}/fee | Returns the blockchain transaction fee.
[**getTransaction**](BlockchainsApi.md#getTransaction) | **GET** /v1/blockchains/{chainId}/transactions/{hash} | Get a single transaction object by its provided &#x60;hash&#x60; on the specified &#x60;chainId&#x60;.
[**submitTransaction**](BlockchainsApi.md#submitTransaction) | **POST** /v1/blockchains/{chainId}/transactions | Submit a transaction to the blockchain.


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

<a name="getAccountInfo"></a>
# **getAccountInfo**
> BlockchainAccountInfo getAccountInfo(chainId, accountId, network, fiat)

Returns account / wallet information for the provided &#x60;chainId&#x60; representing the blockchain.

    Gets account / wallet information like &#x60;balance&#x60;, &#x60;lastTransactionId&#x60; etc of the specified blockchain identified by &#x60;chainId&#x60;.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**| ID of the block chain | [default to null] [enum: xrp, eth]
 **accountId** | **String**| Identifier representing the account. This is most likely the wallet address. | [default to null]
 **network** | **String**|  Blockchain network to get the account info from. For example, for XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint.  | [optional] [default to mainnet]
 **fiat** | **String**| Used to provide equivalent value in specified fiat | [optional] [default to null] [enum: USD, GBP, EUR]

### Return type

[**BlockchainAccountInfo**](../Models/BlockchainAccountInfo.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

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

<a name="getBlockchainStatus"></a>
# **getBlockchainStatus**
> BlockchainStatus getBlockchainStatus(chainId, network)

Returns the status of the provided blockchain.

    Gets status of the provided blockchain identified by &#x60;chainId&#x60;.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**| ID of the block chain | [default to null] [enum: xrp, eth]
 **network** | **String**| Blockchain network to get the status from. | [optional] [default to null]

### Return type

[**BlockchainStatus**](../Models/BlockchainStatus.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

<a name="getBlockchainsAccounts"></a>
# **getBlockchainsAccounts**
> AccountsInfoResponse getBlockchainsAccounts(network, AccountsInfoRequest)

Gets account details of all specified accounts

    Batch call that returns details of all accounts in the request across all specified blockchains.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **network** | **String**| Blockchain network to get the status from. | [optional] [default to null]
 **AccountsInfoRequest** | [**AccountsInfoRequest**](../Models/AccountsInfoRequest.md)| List of accounts with their chainIds to get info for | [optional]

### Return type

[**AccountsInfoResponse**](../Models/AccountsInfoResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json

<a name="getBlockchainsStatus"></a>
# **getBlockchainsStatus**
> AvailableBlockchains getBlockchainsStatus()

Gets a list of all supported blockchains and their statuses.

    Key Connect server keeps track of the individual status of the supported blockchains. Use this API to check the blockchain statuses.

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

Returns the blockchain transaction fee.

    Gets fee of the provided blockchain identified by &#x60;chainId&#x60;.

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **chainId** | **String**| ID of the block chain | [default to null] [enum: xrp, eth]
 **network** | **String**| Blockchain network to get the status from. | [optional] [default to null]

### Return type

[**BlockchainFee**](../Models/BlockchainFee.md)

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

