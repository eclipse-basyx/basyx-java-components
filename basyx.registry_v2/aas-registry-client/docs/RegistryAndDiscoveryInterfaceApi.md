# RegistryAndDiscoveryInterfaceApi

All URIs are relative to */*

Method | HTTP request | Description
------------- | ------------- | -------------
[**deleteAllAssetLinksById**](RegistryAndDiscoveryInterfaceApi.md#deleteAllAssetLinksById) | **DELETE** /lookup/shells/{aasIdentifier} | Deletes all Asset identifier key-value-pair linked to an Asset Administration Shell to edit discoverable content
[**deleteAllShellDescriptors**](RegistryAndDiscoveryInterfaceApi.md#deleteAllShellDescriptors) | **DELETE** /registry/shell-descriptors | Deletes all Asset Administration Shell Descriptors
[**deleteAssetAdministrationShellDescriptorById**](RegistryAndDiscoveryInterfaceApi.md#deleteAssetAdministrationShellDescriptorById) | **DELETE** /registry/shell-descriptors/{aasIdentifier} | Deletes an Asset Administration Shell Descriptor, i.e. de-registers an AAS
[**deleteSubmodelDescriptorById**](RegistryAndDiscoveryInterfaceApi.md#deleteSubmodelDescriptorById) | **DELETE** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} | Deletes a Submodel Descriptor, i.e. de-registers a submodel
[**getAllAssetAdministrationShellDescriptors**](RegistryAndDiscoveryInterfaceApi.md#getAllAssetAdministrationShellDescriptors) | **GET** /registry/shell-descriptors | Returns all Asset Administration Shell Descriptors
[**getAllAssetAdministrationShellIdsByAssetLink**](RegistryAndDiscoveryInterfaceApi.md#getAllAssetAdministrationShellIdsByAssetLink) | **GET** /lookup/shells | Returns a list of Asset Administration Shell ids based on Asset identifier key-value-pairs
[**getAllAssetLinksById**](RegistryAndDiscoveryInterfaceApi.md#getAllAssetLinksById) | **GET** /lookup/shells/{aasIdentifier} | Returns a list of Asset identifier key-value-pairs based on an Asset Administration Shell id to edit discoverable content
[**getAllSubmodelDescriptors**](RegistryAndDiscoveryInterfaceApi.md#getAllSubmodelDescriptors) | **GET** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors | Returns all Submodel Descriptors
[**getAssetAdministrationShellDescriptorById**](RegistryAndDiscoveryInterfaceApi.md#getAssetAdministrationShellDescriptorById) | **GET** /registry/shell-descriptors/{aasIdentifier} | Returns a specific Asset Administration Shell Descriptor
[**getSubmodelDescriptorById**](RegistryAndDiscoveryInterfaceApi.md#getSubmodelDescriptorById) | **GET** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} | Returns a specific Submodel Descriptor
[**postAllAssetLinksById**](RegistryAndDiscoveryInterfaceApi.md#postAllAssetLinksById) | **POST** /lookup/shells/{aasIdentifier} | Creates all Asset identifier key-value-pair linked to an Asset Administration Shell to edit discoverable content
[**postAssetAdministrationShellDescriptor**](RegistryAndDiscoveryInterfaceApi.md#postAssetAdministrationShellDescriptor) | **POST** /registry/shell-descriptors | Creates a new Asset Administration Shell Descriptor, i.e. registers an AAS
[**postSubmodelDescriptor**](RegistryAndDiscoveryInterfaceApi.md#postSubmodelDescriptor) | **POST** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors | Creates a new Submodel Descriptor, i.e. registers a submodel
[**putAssetAdministrationShellDescriptorById**](RegistryAndDiscoveryInterfaceApi.md#putAssetAdministrationShellDescriptorById) | **PUT** /registry/shell-descriptors/{aasIdentifier} | Updates an existing Asset Administration Shell Descriptor
[**putSubmodelDescriptorById**](RegistryAndDiscoveryInterfaceApi.md#putSubmodelDescriptorById) | **PUT** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} | Updates an existing Submodel Descriptor
[**searchShellDescriptors**](RegistryAndDiscoveryInterfaceApi.md#searchShellDescriptors) | **POST** /registry/shell-descriptors/search | 

<a name="deleteAllAssetLinksById"></a>
# **deleteAllAssetLinksById**
> deleteAllAssetLinksById(aasIdentifier)

Deletes all Asset identifier key-value-pair linked to an Asset Administration Shell to edit discoverable content

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
try {
    apiInstance.deleteAllAssetLinksById(aasIdentifier);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#deleteAllAssetLinksById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="deleteAllShellDescriptors"></a>
# **deleteAllShellDescriptors**
> deleteAllShellDescriptors()

Deletes all Asset Administration Shell Descriptors

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
try {
    apiInstance.deleteAllShellDescriptors();
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#deleteAllShellDescriptors");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="deleteAssetAdministrationShellDescriptorById"></a>
# **deleteAssetAdministrationShellDescriptorById**
> deleteAssetAdministrationShellDescriptorById(aasIdentifier)

Deletes an Asset Administration Shell Descriptor, i.e. de-registers an AAS

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
try {
    apiInstance.deleteAssetAdministrationShellDescriptorById(aasIdentifier);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#deleteAssetAdministrationShellDescriptorById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="deleteSubmodelDescriptorById"></a>
# **deleteSubmodelDescriptorById**
> deleteSubmodelDescriptorById(aasIdentifier, submodelIdentifier)

Deletes a Submodel Descriptor, i.e. de-registers a submodel

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
String submodelIdentifier = "submodelIdentifier_example"; // String | The Submodel’s unique id (BASE64-URL-encoded)
try {
    apiInstance.deleteSubmodelDescriptorById(aasIdentifier, submodelIdentifier);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#deleteSubmodelDescriptorById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |
 **submodelIdentifier** | **String**| The Submodel’s unique id (BASE64-URL-encoded) |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

<a name="getAllAssetAdministrationShellDescriptors"></a>
# **getAllAssetAdministrationShellDescriptors**
> List&lt;AssetAdministrationShellDescriptor&gt; getAllAssetAdministrationShellDescriptors()

Returns all Asset Administration Shell Descriptors

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
try {
    List<AssetAdministrationShellDescriptor> result = apiInstance.getAllAssetAdministrationShellDescriptors();
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllAssetAdministrationShellDescriptors");
    e.printStackTrace();
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;AssetAdministrationShellDescriptor&gt;**](AssetAdministrationShellDescriptor.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getAllAssetAdministrationShellIdsByAssetLink"></a>
# **getAllAssetAdministrationShellIdsByAssetLink**
> List&lt;String&gt; getAllAssetAdministrationShellIdsByAssetLink(assetIds)

Returns a list of Asset Administration Shell ids based on Asset identifier key-value-pairs

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
List<IdentifierKeyValuePair> assetIds = Arrays.asList(new IdentifierKeyValuePair()); // List<IdentifierKeyValuePair> | The key-value-pair of an Asset identifier
try {
    List<String> result = apiInstance.getAllAssetAdministrationShellIdsByAssetLink(assetIds);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllAssetAdministrationShellIdsByAssetLink");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **assetIds** | [**List&lt;IdentifierKeyValuePair&gt;**](IdentifierKeyValuePair.md)| The key-value-pair of an Asset identifier | [optional]

### Return type

**List&lt;String&gt;**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getAllAssetLinksById"></a>
# **getAllAssetLinksById**
> List&lt;IdentifierKeyValuePair&gt; getAllAssetLinksById(aasIdentifier)

Returns a list of Asset identifier key-value-pairs based on an Asset Administration Shell id to edit discoverable content

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
try {
    List<IdentifierKeyValuePair> result = apiInstance.getAllAssetLinksById(aasIdentifier);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllAssetLinksById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |

### Return type

[**List&lt;IdentifierKeyValuePair&gt;**](IdentifierKeyValuePair.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getAllSubmodelDescriptors"></a>
# **getAllSubmodelDescriptors**
> List&lt;SubmodelDescriptor&gt; getAllSubmodelDescriptors(aasIdentifier)

Returns all Submodel Descriptors

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
try {
    List<SubmodelDescriptor> result = apiInstance.getAllSubmodelDescriptors(aasIdentifier);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllSubmodelDescriptors");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |

### Return type

[**List&lt;SubmodelDescriptor&gt;**](SubmodelDescriptor.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getAssetAdministrationShellDescriptorById"></a>
# **getAssetAdministrationShellDescriptorById**
> AssetAdministrationShellDescriptor getAssetAdministrationShellDescriptorById(aasIdentifier)

Returns a specific Asset Administration Shell Descriptor

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
try {
    AssetAdministrationShellDescriptor result = apiInstance.getAssetAdministrationShellDescriptorById(aasIdentifier);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAssetAdministrationShellDescriptorById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |

### Return type

[**AssetAdministrationShellDescriptor**](AssetAdministrationShellDescriptor.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="getSubmodelDescriptorById"></a>
# **getSubmodelDescriptorById**
> SubmodelDescriptor getSubmodelDescriptorById(aasIdentifier, submodelIdentifier)

Returns a specific Submodel Descriptor

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
String submodelIdentifier = "submodelIdentifier_example"; // String | The Submodel’s unique id (BASE64-URL-encoded)
try {
    SubmodelDescriptor result = apiInstance.getSubmodelDescriptorById(aasIdentifier, submodelIdentifier);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getSubmodelDescriptorById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |
 **submodelIdentifier** | **String**| The Submodel’s unique id (BASE64-URL-encoded) |

### Return type

[**SubmodelDescriptor**](SubmodelDescriptor.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

<a name="postAllAssetLinksById"></a>
# **postAllAssetLinksById**
> List&lt;IdentifierKeyValuePair&gt; postAllAssetLinksById(body, aasIdentifier)

Creates all Asset identifier key-value-pair linked to an Asset Administration Shell to edit discoverable content

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
List<IdentifierKeyValuePair> body = Arrays.asList(new IdentifierKeyValuePair()); // List<IdentifierKeyValuePair> | Asset identifier key-value-pairs
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
try {
    List<IdentifierKeyValuePair> result = apiInstance.postAllAssetLinksById(body, aasIdentifier);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#postAllAssetLinksById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**List&lt;IdentifierKeyValuePair&gt;**](IdentifierKeyValuePair.md)| Asset identifier key-value-pairs |
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |

### Return type

[**List&lt;IdentifierKeyValuePair&gt;**](IdentifierKeyValuePair.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="postAssetAdministrationShellDescriptor"></a>
# **postAssetAdministrationShellDescriptor**
> AssetAdministrationShellDescriptor postAssetAdministrationShellDescriptor(body)

Creates a new Asset Administration Shell Descriptor, i.e. registers an AAS

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
AssetAdministrationShellDescriptor body = new AssetAdministrationShellDescriptor(); // AssetAdministrationShellDescriptor | Asset Administration Shell Descriptor object
try {
    AssetAdministrationShellDescriptor result = apiInstance.postAssetAdministrationShellDescriptor(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#postAssetAdministrationShellDescriptor");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**AssetAdministrationShellDescriptor**](AssetAdministrationShellDescriptor.md)| Asset Administration Shell Descriptor object |

### Return type

[**AssetAdministrationShellDescriptor**](AssetAdministrationShellDescriptor.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="postSubmodelDescriptor"></a>
# **postSubmodelDescriptor**
> SubmodelDescriptor postSubmodelDescriptor(body, aasIdentifier)

Creates a new Submodel Descriptor, i.e. registers a submodel

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
SubmodelDescriptor body = new SubmodelDescriptor(); // SubmodelDescriptor | Submodel Descriptor object
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
try {
    SubmodelDescriptor result = apiInstance.postSubmodelDescriptor(body, aasIdentifier);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#postSubmodelDescriptor");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**SubmodelDescriptor**](SubmodelDescriptor.md)| Submodel Descriptor object |
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |

### Return type

[**SubmodelDescriptor**](SubmodelDescriptor.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

<a name="putAssetAdministrationShellDescriptorById"></a>
# **putAssetAdministrationShellDescriptorById**
> putAssetAdministrationShellDescriptorById(body, aasIdentifier)

Updates an existing Asset Administration Shell Descriptor

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
AssetAdministrationShellDescriptor body = new AssetAdministrationShellDescriptor(); // AssetAdministrationShellDescriptor | Asset Administration Shell Descriptor object
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
try {
    apiInstance.putAssetAdministrationShellDescriptorById(body, aasIdentifier);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#putAssetAdministrationShellDescriptorById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**AssetAdministrationShellDescriptor**](AssetAdministrationShellDescriptor.md)| Asset Administration Shell Descriptor object |
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

<a name="putSubmodelDescriptorById"></a>
# **putSubmodelDescriptorById**
> putSubmodelDescriptorById(body, aasIdentifier, submodelIdentifier)

Updates an existing Submodel Descriptor

### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
SubmodelDescriptor body = new SubmodelDescriptor(); // SubmodelDescriptor | Submodel Descriptor object
String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id (BASE64-URL-encoded)
String submodelIdentifier = "submodelIdentifier_example"; // String | The Submodel’s unique id (BASE64-URL-encoded)
try {
    apiInstance.putSubmodelDescriptorById(body, aasIdentifier, submodelIdentifier);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#putSubmodelDescriptorById");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**SubmodelDescriptor**](SubmodelDescriptor.md)| Submodel Descriptor object |
 **aasIdentifier** | **String**| The Asset Administration Shell’s unique id (BASE64-URL-encoded) |
 **submodelIdentifier** | **String**| The Submodel’s unique id (BASE64-URL-encoded) |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: Not defined

<a name="searchShellDescriptors"></a>
# **searchShellDescriptors**
> ShellDescriptorSearchResponse searchShellDescriptors(body)



### Example
```java
// Import classes:
//import org.eclipse.basyx.aas.registry.client.ApiException;
//import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;


RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
ShellDescriptorSearchRequest body = new ShellDescriptorSearchRequest(); // ShellDescriptorSearchRequest | 
try {
    ShellDescriptorSearchResponse result = apiInstance.searchShellDescriptors(body);
    System.out.println(result);
} catch (ApiException e) {
    System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#searchShellDescriptors");
    e.printStackTrace();
}
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**ShellDescriptorSearchRequest**](ShellDescriptorSearchRequest.md)|  |

### Return type

[**ShellDescriptorSearchResponse**](ShellDescriptorSearchResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

