# AAS Registry Compatibility

This project provides an *IAASRegistry* implementation using the [aas-registry-client](../aas-registry-client/README.md) as compatibility layer for existing BaSyx components, e.g. the AASServer.

To use the compatibility layer the following two dependencies in your POM file with an appropriate version:


```xml
<dependencies>
    
    <!-- Import generated Java client for DotAAS registry -->
    <dependency>
        <groupId>org.eclipse.basyx.aas.registry</groupId>
        <artifactId>aas-registry-client</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>
    
    <!-- Import basyx compatibility layer for DotAAS registry -->
    <dependency>
        <groupId>org.eclipse.basyx.aas.registry</groupId>
        <artifactId>aas-registry-compatibility</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>
    
</dependencies>
```

You can then create an instance like so: 

```java
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;

public class BaSyxCompatibilityExample {

    public static void main(String[] args) {
        String registryUrl = "someEnpoint";
        
        RegistryAndDiscoveryInterfaceApi apiInstance = new RegistryAndDiscoveryInterfaceApi();
        apiInstance.getApiClient().setBasePath(registryUrl);
        IAASRegistry registryAlternative1 = new DotAASRegistryProxy(apiInstance);
        
        IAASRegistry registryAlternative2 = new DotAASRegistryProxy(registryUrl);
        
    }
}




```
