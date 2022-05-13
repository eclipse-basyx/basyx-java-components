This project uses the [simple-path-generator plugin](../aas-registry-plugins/README.md) to create a builder java class that can be used in conjunction with the AAS registry client to reference a field of an *AssetAdministrationShellDescriptor* document.


To use the path builder class, specify this dependency in your POM file with an appropriate version:


```xml
<dependency>
	<groupId>org.eclipse.basyx.aas.registry</groupId>
	<artifactId>aas-registry-paths</artifactId>
</dependency>
```

Look at the API client project to see how the usage in source code.
