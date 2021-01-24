# AccountsInfoRequestItem
## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**chainId** | [**String**](string.md) | ID of the block chain | [default to null]
**network** | [**String**](string.md) |  Blockchain network to get the account info from. For XRP this would be one of (testnet/devnet/mainnet). More information regarding what environments are available can be obtained from /v1/blockchains/{chainId}/status endpoint. This field is required if &#x60;fee&#x60; is not specified in order to dynamically obtain the fee from the network.  | [optional] [default to mainnet]
**accountId** | [**String**](string.md) | Identifier representing the account. This is most likely the wallet address. | [default to null]

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

