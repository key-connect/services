# StatusApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getBlockchainStatus**](StatusApi.md#getBlockchainStatus) | **GET** /v1/blockchains/{chainId}/status | Returns the status of the provided blockchain.
[**getBlockchainsStatus**](StatusApi.md#getBlockchainsStatus) | **GET** /v1/blockchains/status | Gets a list of all supported blockchains and their statuses.
[**getServerStatus**](StatusApi.md#getServerStatus) | **GET** /v1/server/status | Gets the Key Connect server status.


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

<a name="getServerStatus"></a>
# **getServerStatus**
> ServerStatusResponse getServerStatus()

Gets the Key Connect server status.

    Gets the Key Connect server status along with any error information.

### Parameters
This endpoint does not need any parameter.

### Return type

[**ServerStatusResponse**](../Models/ServerStatusResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json

