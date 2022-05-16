# Asset Administration Registry InMemory Storage

This registry storage implementation uses in-memory hash maps as document store and uses the [base java pojos](../aas-registry-service-base-model/) as data model. Include this dependency if you want to use this storage implementation:

```xml

	<dependency>
		<groupId>org.eclipse.basyx.aas.registry</groupId>
		<artifactId>aas-registry-service-inmemory-storage</artifactId>
	</dependency>
```

Then included, you can active it by either setting the active profile or the *registry.type* attribute:

```
 -Dspring.profiles.active=logEvents,inMemoryStorage
```



