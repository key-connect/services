# BlockchainAccountTransactions
## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**chainId** | [**String**](string.md) |  | [optional] [default to null]
**network** | [**String**](string.md) |  | [optional] [default to null]
**server** | [**String**](string.md) |  | [optional] [default to null]
**accountId** | [**String**](string.md) |  | [optional] [default to null]
**cursor** | [**String**](string.md) | Format varies by the blockchain. For XRP, the value of &#x60;cursor&#x60; will be the &#x60;ledger:seq&#x60; from the marker object. This value is null when there are no more pages. | [optional] [default to null]
**transactions** | [**List**](BlockchainAccountTransactionItem.md) |  | [optional] [default to null]

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

