# Documentation for Key Connect API

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *https://api.keyconnect.app*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*AccountsApi* | [**fundAccount**](Apis/AccountsApi.md#fundaccount) | **POST** /v1/blockchains/{chainId}/accounts/{accountId}/fund | Funds the given account if funding is available for the specified network.
*AccountsApi* | [**getAccountInfo**](Apis/AccountsApi.md#getaccountinfo) | **GET** /v1/blockchains/{chainId}/accounts/{accountId} | Returns account / wallet information for the provided `chainId` representing the blockchain.
*AccountsApi* | [**getAccountPayments**](Apis/AccountsApi.md#getaccountpayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.
*AccountsApi* | [**getAccountTransactions**](Apis/AccountsApi.md#getaccounttransactions) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/transactions | Returns paginated list of transactions.
*AccountsApi* | [**getBlockchainsAccounts**](Apis/AccountsApi.md#getblockchainsaccounts) | **POST** /v1/batch/blockchains/accounts/info | Gets account details of all specified accounts
*BatchApi* | [**getBlockchainsAccounts**](Apis/BatchApi.md#getblockchainsaccounts) | **POST** /v1/batch/blockchains/accounts/info | Gets account details of all specified accounts
*BlockchainsApi* | [**fundAccount**](Apis/BlockchainsApi.md#fundaccount) | **POST** /v1/blockchains/{chainId}/accounts/{accountId}/fund | Funds the given account if funding is available for the specified network.
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
*FundingApi* | [**fundAccount**](Apis/FundingApi.md#fundaccount) | **POST** /v1/blockchains/{chainId}/accounts/{accountId}/fund | Funds the given account if funding is available for the specified network.
*GeneratorApi* | [**generateXrpTransaction**](Apis/GeneratorApi.md#generatexrptransaction) | **GET** /v1/blockchains/xrp/generator/payment | Generates a transaction as a payment.
*MarketsApi* | [**getAggregatedOrderBook**](Apis/MarketsApi.md#getaggregatedorderbook) | **GET** /v1/markets/{base}/{counter}/orderbook | Get aggregated order book for the provided base and counter
*MarketsApi* | [**getAvailableMarkets**](Apis/MarketsApi.md#getavailablemarkets) | **GET** /v1/markets | Get available markets
*MarketsApi* | [**getExchangeOrderBook**](Apis/MarketsApi.md#getexchangeorderbook) | **GET** /v1/markets/{base}/{counter}/exchanges/{exchange}/orderbook | Get order book for the provided base and counter at specified exchange
*PaymentsApi* | [**getAccountPayments**](Apis/PaymentsApi.md#getaccountpayments) | **GET** /v1/blockchains/{chainId}/accounts/{accountId}/payments | Returns paginated list of payments.
*RatesApi* | [**getRate**](Apis/RatesApi.md#getrate) | **GET** /v1/rates/{base}/{counter} | Gets the currency conversion rate for the provided base and counter currency.
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
 - [ErrorResponse](./Models/ErrorResponse.md)
 - [ExceptionalResponse](./Models/ExceptionalResponse.md)
 - [GenericCurrencyValue](./Models/GenericCurrencyValue.md)
 - [InvalidAccountAddressError](./Models/InvalidAccountAddressError.md)
 - [InvalidCursorError](./Models/InvalidCursorError.md)
 - [MarketsItem](./Models/MarketsItem.md)
 - [MarketsResponse](./Models/MarketsResponse.md)
 - [Order](./Models/Order.md)
 - [OrderBook](./Models/OrderBook.md)
 - [Rate](./Models/Rate.md)
 - [RateResponse](./Models/RateResponse.md)
 - [ServerErrorObject](./Models/ServerErrorObject.md)
 - [ServerStatusResponse](./Models/ServerStatusResponse.md)
 - [ServerStatusResponseErrors](./Models/ServerStatusResponseErrors.md)
 - [SubAccountInfo](./Models/SubAccountInfo.md)
 - [SubmitTransactionRequest](./Models/SubmitTransactionRequest.md)
 - [SubmitTransactionResult](./Models/SubmitTransactionResult.md)
 - [TransactionNotFoundError](./Models/TransactionNotFoundError.md)
 - [UnsupportedFundingBlockchainError](./Models/UnsupportedFundingBlockchainError.md)
 - [UnsupportedFundingRequestError](./Models/UnsupportedFundingRequestError.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
