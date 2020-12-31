# Documentation for Key Connect API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://api.keyconnect.app*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*DefaultApi* | [**generateTransaction**](Apis/DefaultApi.md#generatetransaction) | **GET** /v1/blockchains/xrp/generator/payment | Generate a transaction
*DefaultApi* | [**getAccountInfo**](Apis/DefaultApi.md#getaccountinfo) | **GET** /v1/blockchains/{chainId}/accounts/{accountId} | Returns account / wallet information for the provided chainId representing the blockchain.
*DefaultApi* | [**getAccountPayments**](Apis/DefaultApi.md#getaccountpayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Gets payments for a given account on a given blockchain specified by the `chainId` parameter.
*DefaultApi* | [**getAccountTransactions**](Apis/DefaultApi.md#getaccounttransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Gets transactions for given account on a given blockchain specified by the `chainId` parameter.
*DefaultApi* | [**getBlockchainStatus**](Apis/DefaultApi.md#getblockchainstatus) | **GET** /v1/blockchains/{chainId}/status | Returns status of the provided chainId.
*DefaultApi* | [**getBlockchainsStatus**](Apis/DefaultApi.md#getblockchainsstatus) | **GET** /v1/blockchains/status | Returns list of available blockchains and their statuses.
*DefaultApi* | [**getFee**](Apis/DefaultApi.md#getfee) | **GET** /v1/blockchains/{chainId}/fee | Returns fee of the provided chainId.
*DefaultApi* | [**getServerStatus**](Apis/DefaultApi.md#getserverstatus) | **GET** /v1/server/status | Returns status of the server.
*DefaultApi* | [**getTransaction**](Apis/DefaultApi.md#gettransaction) | **GET** /v1/blockchains/{chainId}/transactions/{hash} | Get a single transaction by its hash on the specified `chainId`.
*DefaultApi* | [**submitTransaction**](Apis/DefaultApi.md#submittransaction) | **POST** /v1/blockchains/{chainId}/transactions | Submit a transaction to the blockchain


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
 - [ServerErrorObject](./Models/ServerErrorObject.md)
 - [ServerStatusResponse](./Models/ServerStatusResponse.md)
 - [ServerStatusResponseErrors](./Models/ServerStatusResponseErrors.md)
 - [SubmitTransactionRequest](./Models/SubmitTransactionRequest.md)
 - [SubmitTransactionResult](./Models/SubmitTransactionResult.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
