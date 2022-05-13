# Asset Administration Registry Server Elasticsearch Storage

This registry storage implementation uses [ElasticSearch](https://www.elastic.co/de/elastic-stack/) as document store and generates a specific data model with ElasticSearch annotations. Include this dependency if you want to use this storage implementation:

```xml
	<dependency>
		<groupId>org.eclipse.basyx.aas.registry</groupId>
		<artifactId>aas-registry-service-elasticsearch-storage</artifactId>
	</dependency>
```

Then included, you can activate it by either setting the active profile or the *registry.type* attribute:

```
 -Dspring.profiles.active=logEvents,elasticsearchStorage
```

Dont't forget to also set the elastic search url as property

```
-Delasticsearch.url=127.0.0.1:9200 
```

or use the environment variable

```
ELATICSEARCH_HOST_URL=127.0.0.1:9200
```

