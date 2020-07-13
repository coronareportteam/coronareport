# AdditionalTestControllerApi

All URIs are relative to _/sormas-rest_

| Method                                                                            | HTTP request                         | Description |
| --------------------------------------------------------------------------------- | ------------------------------------ | ----------- |
| [**getAllActiveUuids**](AdditionalTestControllerApi.md#getAllActiveUuids)         | **GET** /additionaltests/uuids       |
| [**getAllAdditionalTests**](AdditionalTestControllerApi.md#getAllAdditionalTests) | **GET** /additionaltests/all/{since} |
| [**getByUuids**](AdditionalTestControllerApi.md#getByUuids)                       | **POST** /additionaltests/query      |
| [**postAdditionalTests**](AdditionalTestControllerApi.md#postAdditionalTests)     | **POST** /additionaltests/push       |

<a name="getAllActiveUuids"></a>

# **getAllActiveUuids**

> List&lt;String&gt; getAllActiveUuids()

### Example

```java
// Import classes:
//import de.quarano.sormas.client.invoker.ApiClient;
//import de.quarano.sormas.client.invoker.ApiException;
//import de.quarano.sormas.client.invoker.Configuration;
//import de.quarano.sormas.client.invoker.auth.*;
//import de.quarano.sormas.client.api.AdditionalTestControllerApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: http-basic
HttpBasicAuth http-basic = (HttpBasicAuth) defaultClient.getAuthentication("http-basic");
http-basic.setUsername("YOUR USERNAME");
http-basic.setPassword("YOUR PASSWORD");

AdditionalTestControllerApi apiInstance = new AdditionalTestControllerApi();
try {
    List<String> result = apiInstance.getAllActiveUuids();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling AdditionalTestControllerApi#getAllActiveUuids");
    e.printStackTrace();
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

**List&lt;String&gt;**

### Authorization

[http-basic](../README.md#http-basic)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json; charset=UTF-8

<a name="getAllAdditionalTests"></a>

# **getAllAdditionalTests**

> List&lt;AdditionalTestDto&gt; getAllAdditionalTests(since)

### Example

```java
// Import classes:
//import de.quarano.sormas.client.invoker.ApiClient;
//import de.quarano.sormas.client.invoker.ApiException;
//import de.quarano.sormas.client.invoker.Configuration;
//import de.quarano.sormas.client.invoker.auth.*;
//import de.quarano.sormas.client.api.AdditionalTestControllerApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: http-basic
HttpBasicAuth http-basic = (HttpBasicAuth) defaultClient.getAuthentication("http-basic");
http-basic.setUsername("YOUR USERNAME");
http-basic.setPassword("YOUR PASSWORD");

AdditionalTestControllerApi apiInstance = new AdditionalTestControllerApi();
Long since = 789L; // Long |
try {
    List<AdditionalTestDto> result = apiInstance.getAllAdditionalTests(since);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling AdditionalTestControllerApi#getAllAdditionalTests");
    e.printStackTrace();
}
```

### Parameters

| Name      | Type     | Description | Notes |
| --------- | -------- | ----------- | ----- |
| **since** | **Long** |             |

### Return type

[**List&lt;AdditionalTestDto&gt;**](AdditionalTestDto.md)

### Authorization

[http-basic](../README.md#http-basic)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json; charset=UTF-8

<a name="getByUuids"></a>

# **getByUuids**

> List&lt;AdditionalTestDto&gt; getByUuids(body)

### Example

```java
// Import classes:
//import de.quarano.sormas.client.invoker.ApiClient;
//import de.quarano.sormas.client.invoker.ApiException;
//import de.quarano.sormas.client.invoker.Configuration;
//import de.quarano.sormas.client.invoker.auth.*;
//import de.quarano.sormas.client.api.AdditionalTestControllerApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: http-basic
HttpBasicAuth http-basic = (HttpBasicAuth) defaultClient.getAuthentication("http-basic");
http-basic.setUsername("YOUR USERNAME");
http-basic.setPassword("YOUR PASSWORD");

AdditionalTestControllerApi apiInstance = new AdditionalTestControllerApi();
List<String> body = Arrays.asList("body_example"); // List<String> |
try {
    List<AdditionalTestDto> result = apiInstance.getByUuids(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling AdditionalTestControllerApi#getByUuids");
    e.printStackTrace();
}
```

### Parameters

| Name     | Type                                | Description | Notes      |
| -------- | ----------------------------------- | ----------- | ---------- |
| **body** | [**List&lt;String&gt;**](String.md) |             | [optional] |

### Return type

[**List&lt;AdditionalTestDto&gt;**](AdditionalTestDto.md)

### Authorization

[http-basic](../README.md#http-basic)

### HTTP request headers

- **Content-Type**: application/json; charset=UTF-8
- **Accept**: application/json; charset=UTF-8

<a name="postAdditionalTests"></a>

# **postAdditionalTests**

> List&lt;PushResult&gt; postAdditionalTests(body)

### Example

```java
// Import classes:
//import de.quarano.sormas.client.invoker.ApiClient;
//import de.quarano.sormas.client.invoker.ApiException;
//import de.quarano.sormas.client.invoker.Configuration;
//import de.quarano.sormas.client.invoker.auth.*;
//import de.quarano.sormas.client.api.AdditionalTestControllerApi;

ApiClient defaultClient = Configuration.getDefaultApiClient();
// Configure HTTP basic authorization: http-basic
HttpBasicAuth http-basic = (HttpBasicAuth) defaultClient.getAuthentication("http-basic");
http-basic.setUsername("YOUR USERNAME");
http-basic.setPassword("YOUR PASSWORD");

AdditionalTestControllerApi apiInstance = new AdditionalTestControllerApi();
List<AdditionalTestDto> body = Arrays.asList(new AdditionalTestDto()); // List<AdditionalTestDto> |
try {
    List<PushResult> result = apiInstance.postAdditionalTests(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling AdditionalTestControllerApi#postAdditionalTests");
    e.printStackTrace();
}
```

### Parameters

| Name     | Type                                                      | Description | Notes      |
| -------- | --------------------------------------------------------- | ----------- | ---------- |
| **body** | [**List&lt;AdditionalTestDto&gt;**](AdditionalTestDto.md) |             | [optional] |

### Return type

[**List&lt;PushResult&gt;**](PushResult.md)

### Authorization

[http-basic](../README.md#http-basic)

### HTTP request headers

- **Content-Type**: application/json; charset=UTF-8
- **Accept**: application/json; charset=UTF-8