# BatchApi

All URIs are relative to *https://api.keyconnect.app*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getBlockchainsAccounts**](BatchApi.md#getBlockchainsAccounts) | **POST** /v1/batch/blockchains/accounts/info | Gets account details of all specified accounts


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

