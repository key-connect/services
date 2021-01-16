# Documentation for Key Connect API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://api.keyconnect.app*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**generateTransaction**](Apis/DefaultApi.md#generatetransaction) | **GET** /v1/blockchains/xrp/generator/payment | Generates a transaction as a payment.
*DefaultApi* | [**getAccountInfo**](Apis/DefaultApi.md#getaccountinfo) | **GET** /v1/blockchains/{chainId}/accounts/{accountId} | Returns account / wallet information for the provided `chainId` representing the blockchain.
*DefaultApi* | [**getAccountPayments**](Apis/DefaultApi.md#getaccountpayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.
*DefaultApi* | [**getAccountTransactions**](Apis/DefaultApi.md#getaccounttransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Returns paginated list of transactions.
*DefaultApi* | [**getBlockchainStatus**](Apis/DefaultApi.md#getblockchainstatus) | **GET** /v1/blockchains/{chainId}/status | Returns the status of the provided blockchain.
*DefaultApi* | [**getBlockchainsStatus**](Apis/DefaultApi.md#getblockchainsstatus) | **GET** /v1/blockchains/status | Gets a list of all supported blockchains and their statuses.
*DefaultApi* | [**getFee**](Apis/DefaultApi.md#getfee) | **GET** /v1/blockchains/{chainId}/fee | Returns the blockchain transaction fee.
*DefaultApi* | [**getServerStatus**](Apis/DefaultApi.md#getserverstatus) | **GET** /v1/server/status | Gets the Key Connect server status.
*DefaultApi* | [**getTransaction**](Apis/DefaultApi.md#gettransaction) | **GET** /v1/blockchains/{chainId}/transactions/{hash} | Get a single transaction object by its provided `hash` on the specified `chainId`.
*DefaultApi* | [**submitTransaction**](Apis/DefaultApi.md#submittransaction) | **POST** /v1/blockchains/{chainId}/transactions | Submit a transaction to the blockchain.


<a name="documentation-for-models"></a>
## Documentation for Models

 - [AvailableBlockchains](./Models/AvailableBlockchains.md)
 - [BlockchainAccountInfo](./Models/BlockchainAccountInfo.md)
 - [BlockchainAccountPaymentItem](./Models/BlockchainAccountPaymentItem.md)
 - [BlockchainAccountPayments](./Models/BlockchainAccountPayments.md)
 - [BlockchainAccountTransaction](./Models/BlockchainAccountTransaction.md)
 - [BlockchainAccountTransactionItem](./Models/BlockchainAccountTransactionItem.md)
 - [BlockchainAccountTransactions](./Models/BlockchainAccountTransactions.md)
 - [BlockchainFee](./Models/BlockchainFee.md)
 - [BlockchainNetwork](./Models/BlockchainNetwork.md)
 - [BlockchainNetworkServerStatus](./Models/BlockchainNetworkServerStatus.md)
 - [BlockchainStatus](./Models/BlockchainStatus.md)
 - [BlockchainStatusResponse](./Models/BlockchainStatusResponse.md)
 - [CurrencyValue](./Models/CurrencyValue.md)
 - [ExceptionalResponse](./Models/ExceptionalResponse.md)
 - [ServerErrorObject](./Models/ServerErrorObject.md)
 - [ServerStatusResponse](./Models/ServerStatusResponse.md)
 - [ServerStatusResponseErrors](./Models/ServerStatusResponseErrors.md)
 - [SubmitTransactionRequest](./Models/SubmitTransactionRequest.md)
 - [SubmitTransactionResult](./Models/SubmitTransactionResult.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
