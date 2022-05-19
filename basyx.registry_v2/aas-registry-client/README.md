# AAS Registry Client

This is the generated java openAPI client that can be used to communicate with the AAS registry server.

To use the client in your maven projects define the following dependency:
```xml
<dependency>
		<groupId>org.eclipse.basyx.aas.registry</groupId>
		<artifactId>aas-registry-client</artifactId>
		<version>0.1.0-SNAPSHOT</version>
</dependency>
```

If you also want to use the search API we highly recommend that you also include the search path builder class. It is also used in an example later in this document:
```xml
<dependency>
		<groupId>org.eclipse.basyx.aas.registry</groupId>
		<artifactId>aas-registry-paths</artifactId>
		<version>0.1.0-SNAPSHOT</version>
</dependency>
```

The search API does not only provide concrete filtering but also similarity matches like words in a longer string. This code, for example, is a match for submodels that contain the word *robot* in their description: 

```java
new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(AasRegistryPaths.submodelDescriptors().description().text()).value("robot");
```

The request is directly mapped to an [ElasticSearch Match query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-match-query.html) on server-site. We also support [these regular expessions](https://www.elastic.co/guide/en/elasticsearch/reference/current/regexp-syntax.html#regexp-optional-operators). A query can be created like this:

```java
new ShellDescriptorQuery().queryType(QueryTypeEnum.REGEX).path(AasRegistryPaths.submodelDescriptors().description().text()).value("r[ob]{3}t");
```

As reponse to a search query, the client will receive a list of the filtered *AssetAdministrationShellDescriptors*. If you have enabled pagination, the list will only contain a subset of all results. By addressing an attribute of a submodel descriptor, the matching descriptors are shrunk so that they contain only matching submodels.

## Getting Started

Path parameters in the request are automatically base64-URL-ecoded if you use the java client api. Ensure that you encode it if you use different clients.```

Here is some generated java code that describes the client usage. 

```java
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id
        try {
            apiInstance.deleteAllAssetLinksById(aasIdentifier);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#deleteAllAssetLinksById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id 
        try {
            apiInstance.deleteAssetAdministrationShellDescriptorById(aasIdentifier);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#deleteAssetAdministrationShellDescriptorById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id 
        String submodelIdentifier = "submodelIdentifier_example"; // String | The Submodel’s unique id 
        try {
            apiInstance.deleteSubmodelDescriptorById(aasIdentifier, submodelIdentifier);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#deleteSubmodelDescriptorById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        try {
            List<AssetAdministrationShellDescriptor> result = apiInstance.getAllAssetAdministrationShellDescriptors();
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllAssetAdministrationShellDescriptors");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        List<IdentifierKeyValuePair> assetIds = Arrays.asList(new IdentifierKeyValuePair()); // List<IdentifierKeyValuePair> | The key-value-pair of an Asset identifier
        try {
            List<String> result = apiInstance.getAllAssetAdministrationShellIdsByAssetLink(assetIds);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllAssetAdministrationShellIdsByAssetLink");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id 
        try {
            List<IdentifierKeyValuePair> result = apiInstance.getAllAssetLinksById(aasIdentifier);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllAssetLinksById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id
        try {
            List<SubmodelDescriptor> result = apiInstance.getAllSubmodelDescriptors(aasIdentifier);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAllSubmodelDescriptors");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id
        try {
            AssetAdministrationShellDescriptor result = apiInstance.getAssetAdministrationShellDescriptorById(aasIdentifier);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getAssetAdministrationShellDescriptorById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id
        String submodelIdentifier = "submodelIdentifier_example"; // String | The Submodel’s unique id
        try {
            SubmodelDescriptor result = apiInstance.getSubmodelDescriptorById(aasIdentifier, submodelIdentifier);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#getSubmodelDescriptorById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        List<IdentifierKeyValuePair> body = Arrays.asList(new IdentifierKeyValuePair()); // List<IdentifierKeyValuePair> | Asset identifier key-value-pairs
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id
        try {
            List<IdentifierKeyValuePair> result = apiInstance.postAllAssetLinksById(body, aasIdentifier);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#postAllAssetLinksById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        AssetAdministrationShellDescriptor body = new AssetAdministrationShellDescriptor(); // AssetAdministrationShellDescriptor | Asset Administration Shell Descriptor object
        try {
            AssetAdministrationShellDescriptor result = apiInstance.postAssetAdministrationShellDescriptor(body);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#postAssetAdministrationShellDescriptor");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        SubmodelDescriptor body = new SubmodelDescriptor(); // SubmodelDescriptor | Submodel Descriptor object
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id
        try {
            SubmodelDescriptor result = apiInstance.postSubmodelDescriptor(body, aasIdentifier);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#postSubmodelDescriptor");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        AssetAdministrationShellDescriptor body = new AssetAdministrationShellDescriptor(); // AssetAdministrationShellDescriptor | Asset Administration Shell Descriptor object
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id
        try {
            apiInstance.putAssetAdministrationShellDescriptorById(body, aasIdentifier);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#putAssetAdministrationShellDescriptorById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        SubmodelDescriptor body = new SubmodelDescriptor(); // SubmodelDescriptor | Submodel Descriptor object
        String aasIdentifier = "aasIdentifier_example"; // String | The Asset Administration Shell’s unique id
        String submodelIdentifier = "submodelIdentifier_example"; // String | The Submodel’s unique id
        try {
            apiInstance.putSubmodelDescriptorById(body, aasIdentifier, submodelIdentifier);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#putSubmodelDescriptorById");
            e.printStackTrace();
        }
    }
}
import org.eclipse.basyx.aas.registry.client.*;
import org.eclipse.basyx.aas.registry.client.auth.*;
import org.eclipse.basyx.aas.registry.model.*;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.basyx.aas.registry.client.api.AasRegistryPaths;

import java.io.File;
import java.util.*;

public class RegistryAndDiscoveryInterfaceApiExample {

    public static void main(String[] args) {
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        ShellDescriptorSearchQuery body = new ShellDescriptorSearchQuery(); // ShellDescriptorSearchQuery | 
		
		// filter by submodels with only german (de-DE) description
		String path = AasRegistryPaths.submodelDescriptors().description().language();
		
		body.setQuery(new ShellDescriptorQuery().queryType(QueryTypeEnum.MATCH).path(path).value("de-DE"));
		// add optional pagination
		body.setPage(new Page().index(0).size(10));
		// optionally sort by identification
		body.setSortBy(new Sorting().addPathItem(SortingPath.IDENTIFICATION));

        try {
            ShellDescriptorSearchResponse result = apiInstance.searchShellDescriptors(body);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling RegistryAndDiscoveryInterfaceApi#searchShellDescriptors");
            e.printStackTrace();
        }
    }
}
```

## Documentation for API Endpoints

All URIs are relative to */*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*RegistryAndDiscoveryInterfaceApi* | [**deleteAllAssetLinksById**](docs/RegistryAndDiscoveryInterfaceApi.md#deleteAllAssetLinksById) | **DELETE** /lookup/shells/{aasIdentifier} | Deletes all Asset identifier key-value-pair linked to an Asset Administration Shell to edit discoverable content
*RegistryAndDiscoveryInterfaceApi* | [**deleteAssetAdministrationShellDescriptorById**](docs/RegistryAndDiscoveryInterfaceApi.md#deleteAssetAdministrationShellDescriptorById) | **DELETE** /registry/shell-descriptors/{aasIdentifier} | Deletes an Asset Administration Shell Descriptor, i.e. de-registers an AAS
*RegistryAndDiscoveryInterfaceApi* | [**deleteSubmodelDescriptorById**](docs/RegistryAndDiscoveryInterfaceApi.md#deleteSubmodelDescriptorById) | **DELETE** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} | Deletes a Submodel Descriptor, i.e. de-registers a submodel
*RegistryAndDiscoveryInterfaceApi* | [**getAllAssetAdministrationShellDescriptors**](docs/RegistryAndDiscoveryInterfaceApi.md#getAllAssetAdministrationShellDescriptors) | **GET** /registry/shell-descriptors | Returns all Asset Administration Shell Descriptors
*RegistryAndDiscoveryInterfaceApi* | [**getAllAssetAdministrationShellIdsByAssetLink**](docs/RegistryAndDiscoveryInterfaceApi.md#getAllAssetAdministrationShellIdsByAssetLink) | **GET** /lookup/shells | Returns a list of Asset Administration Shell ids based on Asset identifier key-value-pairs
*RegistryAndDiscoveryInterfaceApi* | [**getAllAssetLinksById**](docs/RegistryAndDiscoveryInterfaceApi.md#getAllAssetLinksById) | **GET** /lookup/shells/{aasIdentifier} | Returns a list of Asset identifier key-value-pairs based on an Asset Administration Shell id to edit discoverable content
*RegistryAndDiscoveryInterfaceApi* | [**getAllSubmodelDescriptors**](docs/RegistryAndDiscoveryInterfaceApi.md#getAllSubmodelDescriptors) | **GET** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors | Returns all Submodel Descriptors
*RegistryAndDiscoveryInterfaceApi* | [**getAssetAdministrationShellDescriptorById**](docs/RegistryAndDiscoveryInterfaceApi.md#getAssetAdministrationShellDescriptorById) | **GET** /registry/shell-descriptors/{aasIdentifier} | Returns a specific Asset Administration Shell Descriptor
*RegistryAndDiscoveryInterfaceApi* | [**getSubmodelDescriptorById**](docs/RegistryAndDiscoveryInterfaceApi.md#getSubmodelDescriptorById) | **GET** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} | Returns a specific Submodel Descriptor
*RegistryAndDiscoveryInterfaceApi* | [**postAllAssetLinksById**](docs/RegistryAndDiscoveryInterfaceApi.md#postAllAssetLinksById) | **POST** /lookup/shells/{aasIdentifier} | Creates all Asset identifier key-value-pair linked to an Asset Administration Shell to edit discoverable content
*RegistryAndDiscoveryInterfaceApi* | [**postAssetAdministrationShellDescriptor**](docs/RegistryAndDiscoveryInterfaceApi.md#postAssetAdministrationShellDescriptor) | **POST** /registry/shell-descriptors | Creates a new Asset Administration Shell Descriptor, i.e. registers an AAS
*RegistryAndDiscoveryInterfaceApi* | [**postSubmodelDescriptor**](docs/RegistryAndDiscoveryInterfaceApi.md#postSubmodelDescriptor) | **POST** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors | Creates a new Submodel Descriptor, i.e. registers a submodel
*RegistryAndDiscoveryInterfaceApi* | [**putAssetAdministrationShellDescriptorById**](docs/RegistryAndDiscoveryInterfaceApi.md#putAssetAdministrationShellDescriptorById) | **PUT** /registry/shell-descriptors/{aasIdentifier} | Updates an existing Asset Administration Shell Descriptor
*RegistryAndDiscoveryInterfaceApi* | [**putSubmodelDescriptorById**](docs/RegistryAndDiscoveryInterfaceApi.md#putSubmodelDescriptorById) | **PUT** /registry/shell-descriptors/{aasIdentifier}/submodel-descriptors/{submodelIdentifier} | Updates an existing Submodel Descriptor
*RegistryAndDiscoveryInterfaceApi* | [**searchShellDescriptors**](docs/RegistryAndDiscoveryInterfaceApi.md#searchShellDescriptors) | **POST** /registry/shell-descriptors/search | 

## Documentation for Models

 - [AdministrativeInformation](docs/AdministrativeInformation.md)
 - [AssetAdministrationShellDescriptor](docs/AssetAdministrationShellDescriptor.md)
 - [Descriptor](docs/Descriptor.md)
 - [Endpoint](docs/Endpoint.md)
 - [GlobalReference](docs/GlobalReference.md)
 - [HasSemantics](docs/HasSemantics.md)
 - [IdentifierKeyValuePair](docs/IdentifierKeyValuePair.md)
 - [Key](docs/Key.md)
 - [KeyElements](docs/KeyElements.md)
 - [LangString](docs/LangString.md)
 - [MatchQuery](docs/Match.md)
 - [ModelReference](docs/ModelReference.md)
 - [OneOfReference](docs/OneOfReference.md)
 - [Page](docs/Page.md)
 - [ProtocolInformation](docs/ProtocolInformation.md)
 - [Reference](docs/Reference.md)
 - [RegExQuery](docs/RegExQuery.md)
 - [ShellDescriptorQuery](docs/ShellDescriptorQuery.md)
 - [ShellDescriptorSearchRequest](docs/ShellDescriptorSearchRequest.md)
 - [ShellDescriptorSearchResponse](docs/ShellDescriptorSearchResponse.md)
 - [SortDirection](docs/SortDirection.md)
 - [Sorting](docs/Sorting.md)
 - [SortingPath](docs/SortingPath.md)
 - [SubmodelDescriptor](docs/SubmodelDescriptor.md)

## Documentation for Authorization

All endpoints do not require authorization for now.

## Recommendation

It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.

## Author

Gerhard Sonnenberg
gerhard.sonnenberg@dfki.de
[DFKI](https://www.dfki.de)
Senior Software Developer

