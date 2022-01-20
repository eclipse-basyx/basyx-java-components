# Updater Component 

This project is the implementation of master's thesis **Design, Implementation and Evaluation of Asynchroneous Data Source Integration Patterns for Digital Twins enabling Scalable Industrie 4.0.** This framework acts as a middleware which can integrate and make asynchronous communication capability between various data sources which, in the long run helps maintain strict separation and reduce dependency between different the sources. This concepts of service-oriented-architecture and digital twins are also applied here. Different possible architectural solutions are explored here to create a scalable Digital Twin infrastructure as well as a scalable integration of third-party data.

## Prerequisites

The project is implemented using Java programming language and Maven dependency resolver. Thus, the workstation must have Java installed with JAVA_HOME set as an environment variable. Also Maven must also be installed to run the project.

Different data sources are added here using Docker containers. To run the container, the workstation must also have docker and docker-compose installed.


## Project Architecture

The updater is segregated into multiple projects. containing an updater-core and different components for each camel wrapped Source, Sinks and Transformers.

### basyx.components.updater.core
This is the module containing core logics and interfaces of the updater. This module must be included to use the updater. To run the module and include it in your project-

```bash
cd basyx.components.updater.core
mvn clean install
```

Then include in your pom.xml-

```
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.core</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

This will include all the core logics in your project. To run the updater, there is an UpdaterComponent class which expects a RoutesConfiguration. Using the RoutesConfiguration the UpdaterComponent starts the updater with the capability to start multiple routes based on the configurations.

Each route can right now can have a single data source, multiple data transformer in a pipeline fashion and multiple data sinks and/or also a delegator to expose the route via an Http API.

To provide the connection of each routes, a json file must be given which will look like this-

```
[
	{
		"datasource": "property1",
		"transformers": ["jsonataA", "jsonataB"],
		"datasinks": ["ConnectedSubmodel/ConnectedPropertyA", "ConnectedSubmodel/ConnectedPropertyB"]
	},
	{
		"datasource": "property2",
		"transformers": ["jsonataB"],
		"datasinks": ["ConnectedSubmodel/ConnectedPropertyB"],
		"delegator": "delegatorA"
	}
]
```
To extend route connection configurations is to use DefaultRoutesConfigurationFactory which have 2 constructors. one constructor will collect the connection configuration from a default path **routes.json**. But there is another constructor where the location of the json can be sent as a parameter. The following code can be run to add the routes in the configuration-

```
DefaultRoutesConfigurationFactory routesFactory = new DefaultRoutesConfigurationFactory(loader);
configuration.addRoutes(routesFactory.getRouteConfigurations());
```

To extend delegator configurations is to use DefaultDelegatorsConfigurationFactory which have 2 constructors. one constructor will collect the delegator configuration from a default path **delegator.json**. But there is another constructor where the location of the json can be sent as a parameter. The following code can be run to load the delegator configurations-

```
DefaultDelegatorsConfigurationFactory delegatorConfigFactory = new DefaultDelegatorsConfigurationFactory(loader);
configuration.addDelegators(delegatorConfigFactory.getDelegatorConfigurations());
```

Delegators can be configured by json files like below-

```
[
	{
		"uniqueId": "DelegatorA",
		"host": "localhost",
		"path": "/valueA",
		"port": 8090
	},
	{
		"uniqueId": "DelegatorB",
		"host": "localhost",
		"path": "/valueB",
		"port": 8091
	}
]
```

If a route contains a delegator, then the route will not be started right away. The delegator will expose an API endpoint based on the delegator configurations and upon receiving a request, it will start the route, receive one message, stop the route again and return the retrieved message as a response.


#### basyx.components.updater.camel-aas
This is the module containing the custom camel component of the Basyx Asset administration shell. To run and include the module. first core component must be added to project. Then-

```bash
cd basyx.components.updater.camel-aas
mvn clean install
```

Then include in your pom.xml-

```
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.camel-aas</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

This will include The component in the project. There are 2 ways to build the RoutesConfiguration from AAS. BasyxRoutesConfigurationBuilder can be used if a registry and an aasId is provided. Then the configurations will be taken from the AAS server itself and then the UpdaterComponent can be invoked to run the server. Currently, only MQTT data source is supported in this way. 

Another option to collect AAS configuration is to use AASProducerDefaultConfigurationFactory which have 2 constructors. one constructor will collect the aas configuration from a default path **aasserver.json**. But there is another constructor where the location of the json can be sent as a parameter. The following code can be run to load the aas data sink configurations-

```
AASProducerDefaultConfigurationFactory aasConfigFactory = new AASProducerDefaultConfigurationFactory(loader);
configuration.addDatasinks(aasConfigFactory.getDataSinkConfigurations());
```

In the json file, the following properties has to be provided-

```
[
	{
		"uniqueId": "ConnectedSubmodel/ConnectedPropertyA",
		"endpoint": "http://localhost:4001/shells/TestUpdatedDeviceAAS/aas",
		"path": "ConnectedSubmodel/ConnectedPropertyA"
	},
	{
		"uniqueId": "ConnectedSubmodel/ConnectedPropertyB",
		"endpoint": "http://localhost:4001/shells/TestUpdatedDeviceAAS/aas",
		"path": "ConnectedSubmodel/ConnectedPropertyB"
	}
]
```
Multiple AAS data sinks can be provided in this way. Where each must have different uniqueId to finally map in each route.

#### basyx.components.updater.camel-kafka
This is the module containing the camel component of the Apache Kafka. To run and include the module. first core component must be added to project. Then-

```bash
cd basyx.components.updater.camel-kafka
mvn clean install
```

Then include in your pom.xml-

```
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.camel-kafka</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

This will include The component in the project. To extend kafka configurations is to use KafkaDefaultConfigurationFactory which have 2 constructors. one constructor will collect the kafka configuration from a default path **kafkaconsumer.json**. But there is another constructor where the location of the json can be sent as a parameter. The following code can be run to load the kafka configurations-

```
KafkaDefaultConfigurationFactory kafkaConfigFactory = new KafkaDefaultConfigurationFactory(loader);
configuration.addDatasources(kafkaConfigFactory.getDataSourceConfigurations());
```

In the json file, the following properties has to be provided-

```
[
	{
		"uniqueId": "property1",
		"serverUrl": "localhost",
		"serverPort": 9092,
		"topic": "first-topic",
		"maxPollRecords": 5000,
		"groupId": "basyx-updater",
		"consumersCount": 1,
		"seekTo": "latest"
	},
	{
		"uniqueId": "property2",
		"serverUrl": "localhost",
		"serverPort": 9092,
		"topic": "second-topic",
		"maxPollRecords": 5000,
		"groupId": "basyx-updater",
		"consumersCount": 1,
		"seekTo": "latest"
	}
]
```
Multiple Kafka data sources can be provided in this way. Where each must have different uniqueId to finally map in each route.

#### basyx.components.updater.camel-activemq
This is the module containing the camel component of the ActiveMQ. To run and include the module. first core component must be added to project. Then-

```bash
cd basyx.components.updater.camel-activemq
mvn clean install
```

Then include in your pom.xml-

```
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.camel-activemq</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

This will include The component in the project. To extend ActiveMQ configurations is to use ActiveMQDefaultConfigurationFactory which have 2 constructors. one constructor will collect the activemq configuration from a default path **activemqconsumer.json**. But there is another constructor where the location of the json can be sent as a parameter. The following code can be run to load activemq configurations-

```
ActiveMQDefaultConfigurationFactory activeMQConfigFactory = new ActiveMQDefaultConfigurationFactory(loader);
configuration.addDatasources(activeMQConfigFactory.getDataSourceConfigurations());
```

In the json file, the following properties has to be provided-

```
[
	{
		"uniqueId": "property1",
		"serverUrl": "127.0.0.1",
		"serverPort": 61616,
		"queue": "first-topic"
	},
	{
		"uniqueId": "property2",
		"serverUrl": "127.0.0.1",
		"serverPort": 9092,
		"queue": "second-topic"
	}
]
```
Multiple ActiveMQ data sources can be provided in this way. Where each must have different uniqueID to finally map in each route.


#### basyx.components.updater.camel-paho
This is the module containing the camel component of the MQTT. To run and include the module. first core component must be added to project. Then-

```bash
cd basyx.components.updater.camel-paho
mvn clean install
```

Then include in your pom.xml-

```
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.camel-paho</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

This will include The component in the project. To extend MQTT configurations is to use MqttDefaultConfigurationFactory which have 2 constructors. one constructor will collect the mqtt configuration from a default path **mqttconsumer.json**. But there is another constructor where the location of the json can be sent as a parameter. The following code can be run to load mqtt configurations-

```
MqttDefaultConfigurationFactory mqttConfigFactory = new MqttDefaultConfigurationFactory(loader);
configuration.addDatasources(mqttConfigFactory.getDataSourceConfigurations());
```

In the json file, the following properties has to be provided-

```
[
	{
		"uniqueId": "property1",
		"serverUrl": "localhost",
		"serverPort": 1884,
		"topic": "Properties"
	},
	{
		"uniqueId": "property2",
		"serverUrl": "localhost",
		"serverPort": 1884,
		"topic": "PropertyB"
	}
]
```
Multiple MQTT data sources can be provided in this way. Where each must have different uniqueID to finally map in each route.


#### basyx.components.updater.transformer.camel-jsonata
This is the module containing the camel component of the Jsonata. To run and include the module. first core component must be added to project. Then-

```bash
cd basyx.components.updater.tranformer.camel-jsonata
mvn clean install
```

Then include in your pom.xml-

```
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.transformer.camel-jsonata</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

This will include The component in the project. To extend Jsonata configurations is to use JsonataDefaultConfigurationFactory which have 2 constructors. one constructor will collect the jsonata configuration from a default path **jsontatatransformer.json**. But there is another constructor where the location of the json can be sent as a parameter. The following code can be run to load the jsonata configurations-

```
JsonataDefaultConfigurationFactory jsonataConfigFactory = new JsonataDefaultConfigurationFactory(loader);
configuration.addTransformers(jsonataConfigFactory.getDataTransformerConfigurations());
```

In the json file, the following properties has to be provided-

```
[
	{
		"uniqueId": "jsonataA",
		"queryPath": "classpath://C:/Users/ashha/Desktop/Development/Thesis/updater-component/basyx.components.updater.examples/basyx.components.updater.examples.mqtt-jsonata-aas/src/main/resources/jsonataA.json",
		"inputType": "JsonString",
		"outputType": "JsonString"
	},
	{
		"uniqueId": "jsonataB",
		"queryPath": "file://C:/Users/ashha/Desktop/Development/Thesis/updater-component/basyx.components.updater.examples/basyx.components.updater.examples.mqtt-jsonata-aas/src/main/resources/jsonataB.json",
		"inputType": "JsonString",
		"outputType": "JsonString"
	}
]
```
Multiple Jsonata data transformers can be provided in this way. queryPath is the location of a file where the query is written. Different ways to put queryPath is given in this [link](https://camel.apache.org/components/3.11.x/jsonata-component.html#_path_parameters_1_parameters). each must have different uniqueID to finally map in each route.


Use the [maven](https://maven.apache.org/download.cgi) to install this project.

### An integration example between Kafka, Jsonata and AAS

Include the core, jsonata, kafka and aas component to the project-
```
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.core</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
		
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.camel-kafka</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.camel-aas</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.transformer.camel-jsonata</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

Then create the corresponding json configuration in the source and extend the configurations-
```
// To load the jsons from resource folder
ClassLoader loader = this.getClass().getClassLoader();
RoutesConfiguration configuration = new RoutesConfiguration();

// Extend configutation for connections
DefaultRoutesConfigurationFactory routesFactory = new DefaultRoutesConfigurationFactory(loader);
configuration.addRoutes(routesFactory.getRouteConfigurations());

// Extend configutation for Kafka Source
KafkaDefaultConfigurationFactory kafkaConfigFactory = new KafkaDefaultConfigurationFactory(loader);
configuration.addDatasources(kafkaConfigFactory.getDataSourceConfigurations());

// Extend configuration for AAS
AASProducerDefaultConfigurationFactory aasConfigFactory = new AASProducerDefaultConfigurationFactory(loader);
configuration.addDatasinks(aasConfigFactory.getDataSinkConfigurations());

// Extend configuration for Jsonata
JsonataDefaultConfigurationFactory jsonataConfigFactory = new JsonataDefaultConfigurationFactory(loader);
configuration.addTransformers(jsonataConfigFactory.getDataTransformerConfigurations());

updater = new UpdaterComponent(configuration);
updater.startComponent();

```

Then the updater will run with the routes from the configurations.

### An integration example between ActiveMQ, Jsonata and AAS with a delegator route and one normal route

Include the core, jsonata, activeMQ and aas component to the project-
```
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.core</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
		
<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.camel-activemq</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.camel-aas</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>

<dependency>
	<groupId>org.eclipse.basyx</groupId>
	<artifactId>basyx.components.updater.transformer.camel-jsonata</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

routes.json has one delegated route and one normal routes like given below- 
```
[
	{
		"datasource": "property1",
		"transformers": ["jsonataA"],
		"datasinks": [],
		"delegator": "DelegatorA"
	},
	{
		"datasource": "property2",
		"transformers": ["jsonataB"],
		"datasinks": ["ConnectedSubmodel/ConnectedPropertyB"]
	}
]
```

After providing all necessary json configuration files for activemq, jsonata, delegator and aas, the following code can load the configurations and run the updater-

```
        ClassLoader loader = TestAASUpdater.class.getClassLoader();
		RoutesConfiguration configuration = new RoutesConfiguration();

		// Extend configutation for connections
		DefaultRoutesConfigurationFactory routesFactory = new DefaultRoutesConfigurationFactory(loader);
		configuration.addRoutes(routesFactory.getRouteConfigurations());

		// Extend configutation for Kafka Source
		ActiveMQDefaultConfigurationFactory activeMQConfigFactory = new ActiveMQDefaultConfigurationFactory(loader);
		configuration.addDatasources(activeMQConfigFactory.getDataSourceConfigurations());

		// Extend configuration for AAS
		AASProducerDefaultConfigurationFactory aasConfigFactory = new AASProducerDefaultConfigurationFactory(loader);
		configuration.addDatasinks(aasConfigFactory.getDataSinkConfigurations());

		// Extend configuration for Delegator
		DefaultDelegatorsConfigurationFactory delegatorConfigFactory = new DefaultDelegatorsConfigurationFactory(loader);
		configuration.addDelegators(delegatorConfigFactory.getDelegatorConfigurations());

		// Extend configuration for Jsonata
		JsonataDefaultConfigurationFactory jsonataConfigFactory = new JsonataDefaultConfigurationFactory(loader);
		configuration.addTransformers(jsonataConfigFactory.getDataTransformerConfigurations());

		updater = new UpdaterComponent(configuration);
		updater.startComponent();

```


In the **basyx.components.updater.examples** there are many integration examples between kafka, activemq, paho, aas internal, jsonata, delegator routes etc. Take a look at them.


#### Example
##### Running Single Zookeeper Single Broker Kafka server
To run the single zookeeper single broker kafka server, docker needs to be installed in the machine. docker-compose must be accessible from the CLI. Open up a bash in the clone directory and run the below command-
```bash
cd basyx.components.updater.examples\kafka-single-broker-server
docker-compose up
```
This will run the zookeeper server at port 2181 and kafka server at 9092

##### Running Single Zookeeper Multiple Broker Kafka server
To run the single zookeeper multiple broker kafka server, docker needs to be installed in the machine. docker-compose must be accessible from the CLI. Open up a bash in the clone directory and run the below command-
```bash
cd basyx.components.updater.examples\kafka-multiple-zookeeper-single-server
docker-compose up
```
This will run the zookeeper server at port 2181 and multiple broker kafka server at 9092,9093 and 9094. This will be helpful to create topic with replication factor more than 1. This will increase reliability of the system.

##### Running Multiple Zookeeper Multiple Broker Kafka server
To run the multiple zookeeper multiple broker kafka server, docker needs to be installed in the machine. docker-compose must be accessible from the CLI. Open up a bash in the clone directory and run the below command-
```bash
cd basyx.components.updater.examples\kafka-multiple-zookeeper-multiple-server
docker-compose up
```
This will run the zookeeper server at port 2181,2182 and 2183 and kafka server at 9092,9093 and 9094. This will increase reliability as well as availability of the system if a zookeeper is down

##### Running ActiveMQ Server
To run the activemq server, docker needs to be installed in the machine. docker-compose must be accessible from the CLI. Open up a bash in the clone directory and run the below command-
```bash
cd basyx.components.updater.examples\active-mq-server
docker-compose up
```
This will run the activemq server at port 61616 

##### Running Dummy Kafka Producer
To run a dummy Kafka producer, Open up a bash in the clone directory and run the below command-
```bash
cd basyx.components.updater.examples\basyx.components.updater.examples.kafkaproducer
mvn clean install
mvn exec:java
```
This will run a dummy kafka producer which will generate 100 JSON message in 100 seconds. The message will be in JSON format with id and temperature property.

##### Running Dummy ActiveMQ Producer
To run a dummy ActiveMQ producer, Open up a bash in the clone directory and run the below command-
```bash
cd basyx.components.updater.examples\basyx.components.updater.examples.activemqproducer
mvn clean install
mvn exec:java
```
This will run a dummy activemq producer which will generate 100 JSON message in 100 seconds. The message will be in JSON format with id and temperature property.

##### Running AAS Server
To run a dummy AAS server, Open up a bash in the clone directory and run the below command-
```bash
cd basyx.components.updater.examples\basyx.components.updater.examples.aasserver
mvn exec:java
```
This will run an AAS Server at http://localhost:4001/aasServer/shells
Current setting of the Updater will update the submodel element value at-
http://localhost:4001/aasServer/shells/eclipse.basyx.aas.oven/aas/submodels/documentationSm/submodel/submodelElements/maxTemp/value

If Kafka server, AAS Server, Updater and Kafka Producer are run in this order, the output will be reflected in the value property of the submodel element.

#### Load Testing using Jmeter
Jmeter can be used to perform load test/ performance tests of the Updater component. To use Jmeter go to https://jmeter.apache.org/ and download Jmeter, build and run the Jar file.

##### Kafka Load Testing
To Load test using kafka, go to https://github.com/GSLabDev/pepper-box/archive/v1.0.tar.gz. This will download the compressed project. Decompress the project and build the project using java compiler. This will create a jar file. Add the jar to-
```bash
[Jmeter Directory]\lib\ext
```
And restart Jmeter. Then you can use Jmeter to open the .jmx file situated in the-
```bash
basyx.components.updater.examples\Kafka Jmeter Load Test
```
If the kafka server is running, this .jmx file can be used to load test kafka producer