# AccountsApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**fundAccount**](AccountsApi.md#fundAccount) | **POST** /v1/blockchains/{chainId}/accounts/{accountId}/fund | Funds the given account if funding is available for the specified network.
[**getAccountInfo**](AccountsApi.md#getAccountInfo) | **GET** /v1/blockchains/{chainId}/accounts/{accountId} | Returns account / wallet information for the provided &#x60;chainId&#x60; representing the blockchain.
[**getAccountPayments**](AccountsApi.md#getAccountPayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.
[**getAccountTransactions**](AccountsApi.md#getAccountTransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Returns paginated list of transactions.
[**getBlockchainsAccounts**](AccountsApi.md#getBlockchainsAccounts) | **POST** /v1/batch/blockchains/accounts/info | Gets account details of all specified accounts


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

