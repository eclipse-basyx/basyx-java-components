# AAS Registry Plugins

This project provides two maven plugins.

Using the first plugin, you can perform an overlay operation based on two YAML files. This could be quite useful when you want to extend existing openAPI definitions or want to enhance a definition with annotations that should be processed by an openAPI-generator.

To use the plugin embed this snippet into your POM file and specify an appropriate version:

``` xml
<plugin>
	<groupId>org.eclipse.basyx.aas.registry</groupId>
	<artifactId>aas-registry-plugins</artifactId>
	<executions>
		<execution>
			<goals>
				<goal>yaml-overlay</goal>
			</goals>
		</execution>
	</executions>
	<configuration>
		<base>${project.basedir}/base.yaml</base>
		<overlay>${project.basedir}/overlay.yaml</overlay>
		<out>${project.basedir}/result.yaml</out>
	</configuration>
</plugin>
```
The other maven plugin can be used to generate builder classes that create search paths for ElasticSearch based POJO classes. The plugin traverses the referenced class and its fields and generates a class that can be used to set up search paths that reference elements in an ElasticSearch document. 

As we use the same search path as elastics in our AAS registry client, this generator can also be used there. The main benefit is that we will avoid typos when using the generated client and do not need to specify the string directly.

In addition this plugin also generates a class that can be used to resolve a field of an object referenced by a path.

This is how you embed it into your POM file:

``` xml 
<plugin>
	<groupId>org.eclipse.basyx.aas.registry</groupId>
	<artifactId>aas-registry-plugins</artifactId>
	<executions>
		<execution>
			<id>paths</id>
			<goals>
				<goal>simple-path-generator</goal>
			</goals>
		</execution>
	</executions>
	<configuration>
		<pathsTargetClassName>AasRegistryPaths</pathsTargetClassName>
		<processorTargetClassName>AasRegistryPathProcessor</processorTargetClassName>
		<className>org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor</className>
		<targetSourceFolder>${project.basedir}/src/generated/java</targetSourceFolder>
		<targetPackageName>org.eclipse.basyx.aas.registry.client.api</targetPackageName>
	</configuration>
</plugin>
```

We use a mustache template for the java class that will be generated. It will be thus quite easy to extend our plugin and add an additional mustache file for a different language.