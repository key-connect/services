# Documentation for Key Connect API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://api.keyconnect.app*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AccountsApi* | [**getAccountInfo**](Apis/AccountsApi.md#getaccountinfo) | **GET** /v1/blockchains/{chainId}/accounts/{accountId} | Returns account / wallet information for the provided `chainId` representing the blockchain.
*AccountsApi* | [**getAccountPayments**](Apis/AccountsApi.md#getaccountpayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.
*AccountsApi* | [**getAccountTransactions**](Apis/AccountsApi.md#getaccounttransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Returns paginated list of transactions.
*AccountsApi* | [**getBlockchainsAccounts**](Apis/AccountsApi.md#getblockchainsaccounts) | **POST** /v1/batch/blockchains/accounts/info | Gets account details of all specified accounts
*BatchApi* | [**getBlockchainsAccounts**](Apis/BatchApi.md#getblockchainsaccounts) | **POST** /v1/batch/blockchains/accounts/info | Gets account details of all specified accounts
*BlockchainsApi* | [**generateXrpTransaction**](Apis/BlockchainsApi.md#generatexrptransaction) | **GET** /v1/blockchains/xrp/generator/payment | Generates a transaction as a payment.
*BlockchainsApi* | [**getAccountInfo**](Apis/BlockchainsApi.md#getaccountinfo) | **GET** /v1/blockchains/{chainId}/accounts/{accountId} | Returns account / wallet information for the provided `chainId` representing the blockchain.
*BlockchainsApi* | [**getAccountPayments**](Apis/BlockchainsApi.md#getaccountpayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.
*BlockchainsApi* | [**getAccountTransactions**](Apis/BlockchainsApi.md#getaccounttransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Returns paginated list of transactions.
*BlockchainsApi* | [**getBlockchainStatus**](Apis/BlockchainsApi.md#getblockchainstatus) | **GET** /v1/blockchains/{chainId}/status | Returns the status of the provided blockchain.
*BlockchainsApi* | [**getBlockchainsAccounts**](Apis/BlockchainsApi.md#getblockchainsaccounts) | **POST** /v1/batch/blockchains/accounts/info | Gets account details of all specified accounts
*BlockchainsApi* | [**getBlockchainsStatus**](Apis/BlockchainsApi.md#getblockchainsstatus) | **GET** /v1/blockchains/status | Gets a list of all supported blockchains and their statuses.
*BlockchainsApi* | [**getFee**](Apis/BlockchainsApi.md#getfee) | **GET** /v1/blockchains/{chainId}/fee | Returns the blockchain transaction fee.
*BlockchainsApi* | [**getTransaction**](Apis/BlockchainsApi.md#gettransaction) | **GET** /v1/blockchains/{chainId}/transactions/{hash} | Get a single transaction object by its provided `hash` on the specified `chainId`.
*BlockchainsApi* | [**submitTransaction**](Apis/BlockchainsApi.md#submittransaction) | **POST** /v1/blockchains/{chainId}/transactions | Submit a transaction to the blockchain.
*GeneratorApi* | [**generateXrpTransaction**](Apis/GeneratorApi.md#generatexrptransaction) | **GET** /v1/blockchains/xrp/generator/payment | Generates a transaction as a payment.
*PaymentsApi* | [**getAccountPayments**](Apis/PaymentsApi.md#getaccountpayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.
*ServerApi* | [**getServerStatus**](Apis/ServerApi.md#getserverstatus) | **GET** /v1/server/status | Gets the Key Connect server status.
*StatusApi* | [**getBlockchainStatus**](Apis/StatusApi.md#getblockchainstatus) | **GET** /v1/blockchains/{chainId}/status | Returns the status of the provided blockchain.
*StatusApi* | [**getBlockchainsStatus**](Apis/StatusApi.md#getblockchainsstatus) | **GET** /v1/blockchains/status | Gets a list of all supported blockchains and their statuses.
*StatusApi* | [**getServerStatus**](Apis/StatusApi.md#getserverstatus) | **GET** /v1/server/status | Gets the Key Connect server status.
*TransactionsApi* | [**getAccountTransactions**](Apis/TransactionsApi.md#getaccounttransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Returns paginated list of transactions.
*TransactionsApi* | [**getTransaction**](Apis/TransactionsApi.md#gettransaction) | **GET** /v1/blockchains/{chainId}/transactions/{hash} | Get a single transaction object by its provided `hash` on the specified `chainId`.
*TransactionsApi* | [**submitTransaction**](Apis/TransactionsApi.md#submittransaction) | **POST** /v1/blockchains/{chainId}/transactions | Submit a transaction to the blockchain.


<a name="documentation-for-models"></a>
## Documentation for Models

 - [AccountsInfoRequest](./Models/AccountsInfoRequest.md)
 - [AccountsInfoRequestItem](./Models/AccountsInfoRequestItem.md)
 - [AccountsInfoResponse](./Models/AccountsInfoResponse.md)
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
 - [GenericCurrencyValue](./Models/GenericCurrencyValue.md)
 - [ServerErrorObject](./Models/ServerErrorObject.md)
 - [ServerStatusResponse](./Models/ServerStatusResponse.md)
 - [ServerStatusResponseErrors](./Models/ServerStatusResponseErrors.md)
 - [SubAccountInfo](./Models/SubAccountInfo.md)
 - [SubmitTransactionRequest](./Models/SubmitTransactionRequest.md)
 - [SubmitTransactionResult](./Models/SubmitTransactionResult.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
