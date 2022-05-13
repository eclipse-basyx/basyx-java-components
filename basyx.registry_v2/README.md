# Asset Administration Shell Registry

This in a Java-based implementation of the Asset Adminstration Shell Registry server and client based on the corresponding [Open-API specification](https://api.swaggerhub.com/apis/Plattform_i40/Registry-and-Discovery/Final-Draft/swagger.yaml?resolved=true) of the German Plattform Industrie 4.0 and its specification document [Details of the Asset Administration Shell, Part 2, v1.0RC02](https://www.plattform-i40.de/IP/Redaktion/EN/Downloads/Publikation/Details_of_the_Asset_Administration_Shell_Part2_V1.html)

[aas-registry-client](aas-registry-client/README.md) can be used to interact with the backend to register or unregister descriptors and submodels or perform search operations.

[aas-registry-compatibility](aas-registry-compatibility/README.md) provides a IAASRegistry implementation using the aas-registry-client as compatibility layer for existing BaSyx components, e.g. the AASServer.

[aas-registry-events](aas-registry-events/README.md) provides classes to deserialize aas-registry events and an event sink interface.

[aas-registry-paths](aas-registry-paths/README.md) generates a builder class that can be used by the registry client to create search requests. 

[aas-registry-plugins](aas-registry-plugins/README.md) contains maven plugins used by the other projects, so you need to build this project first.

[aas-registry-service](aas-registry-service/README.md) provides the application server to access the AAS descriptor storage and offers an API for REST-based communication.

[aas-registry-service-base-model](aas-registry-service-bases-model/README.md) provides a base model implementation that should be used if you do not need specific model annotations for your storage. It is used for the inmemory storage implementation and you need to add it explicitely as dependency for your server deployment as it is defined as 'provided' dependency in the [aas-registry-service](aas-registry-service/README.md) POM.

[aas-registry-service-base-tests](aas-registry-service-base-tests/README.md) provides helper classes and abstract test classes that can be extended in storage tests or integration tests. The abstract test classes already define test methods so that you will get a good test coverage without writing any additional test cases.

[aas-registry-service-elasticsearch-storage](aas-registry-service-elasticsearch/README.md) provides a registry-storage implementation based on elasticsearch that could be used as storage for [aas-registry-service](aas-registry-service/README.md). It comes with its own java-based model classes, annotated with elasticsearch annotations.

[aas-registry-service-inmemory-storage](aas-registry-service-inmemmory-storage/README.md) provides a non-persistent registry-storage implementation there instances are stored in hash maps. It can be used as storage for [aas-registry-service](aas-registry-service/README.md).

[aas-registry-kafka-events](aas-registry-kafka-events/README.md) extends the aas-registry-service with a registry-event-sink implementation that delivers shell descriptor and submodel registration events using Apache Kafka. The default provided by aas-registry-service just logs the events.

[aas-registry-service-release-kafka-es](aas-registry-service-release-kafka-es/README.md) is used to combine the server artifacts to a release image that uses [Apache Kafka](https://kafka.apache.org/) as event sink and [ElasticSearch](https://www.elastic.co/de/elasticsearch/) as storage.

[aas-registry-service-release-kafka-mem](aas-registry-service-release-kafka-mem/README.md) is used to combine the server artifacts to a release image that uses Apache Kafka as event sink and an in-memory storage.

[aas-registry-service-release-log-es](aas-registry-service-release-log-es/README.md) is used to combine the server artifacts to a release image that logs registry events and uses ElasticSearch as data storage.

[aas-registry-service-release-log-mem](aas-registry-service-release-log-es/README.md) is used to combine the server artifacts to a release image that logs registry events and an in-memory storage.

The docker images can be found [here] (https://hub.docker.com/r/dfkibasys/aas-registry-dotaas-part2/).

A docker compose file that illustrates the setup can be found in the [docker-compose](docker-compose/docker-compose.yml) folder.