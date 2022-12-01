/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.registry;

import java.util.Arrays;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.IComponent;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxMqttConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSQLConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration.AuthorizationStrategy;
import org.eclipse.basyx.components.registry.authorization.GrantedAuthoritySecurityFeature;
import org.eclipse.basyx.components.registry.authorization.SecurityFeature;
import org.eclipse.basyx.components.registry.authorization.SimpleRbacSecurityFeature;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.eclipse.basyx.components.registry.configuration.RegistryEventBackend;
import org.eclipse.basyx.components.registry.mongodb.MongoDBRegistry;
import org.eclipse.basyx.components.registry.mongodb.MongoDBTaggedDirectory;
import org.eclipse.basyx.components.registry.mqtt.MqttRegistryFactory;
import org.eclipse.basyx.components.registry.mqtt.MqttTaggedDirectoryFactory;
import org.eclipse.basyx.components.registry.mqtt.MqttV2RegistryFactory;
import org.eclipse.basyx.components.registry.mqtt.MqttV2TaggedDirectoryFactory;
import org.eclipse.basyx.components.registry.servlet.RegistryServlet;
import org.eclipse.basyx.components.registry.servlet.TaggedDirectoryServlet;
import org.eclipse.basyx.components.registry.sql.SQLRegistry;
import org.eclipse.basyx.components.security.authorization.IJwtBearerTokenAuthenticationConfigurationProvider;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.map.MapTaggedDirectory;
import org.eclipse.basyx.extensions.shared.encoding.Base64URLEncoder;
import org.eclipse.basyx.extensions.shared.encoding.URLEncoder;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxHTTPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic registry that can start and stop a registry with different kinds of
 * backends. Currently supports MongoDB and SQL. For development purposes, the
 * component can also start a registry without a backend and without
 * persistency.
 * 
 * @author espen, wege
 *
 */
public class RegistryComponent implements IComponent {
	private static Logger logger = LoggerFactory.getLogger(RegistryComponent.class);

	// The server with the servlet that will be created
	private BaSyxHTTPServer server;

	// The component configuration
	private BaSyxContextConfiguration contextConfig;
	private BaSyxRegistryConfiguration registryConfig;

	// The backend configuration
	private BaSyxMongoDBConfiguration mongoDBConfig;
	private BaSyxSQLConfiguration sqlConfig;
	private BaSyxMqttConfiguration mqttConfig;
	private BaSyxSecurityConfiguration securityConfig;

	/**
	 * Default constructor that loads default configurations
	 */
	public RegistryComponent() {
		contextConfig = new BaSyxContextConfiguration();
		registryConfig = new BaSyxRegistryConfiguration();
	}

	/**
	 * Constructor with given configuration for the registry and its server context.
	 * This constructor will create an InMemory registry.
	 * 
	 * @param contextConfig
	 *            The context configuration
	 */
	public RegistryComponent(BaSyxContextConfiguration contextConfig) {
		this.contextConfig = contextConfig;
		this.registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);
	}

	/**
	 * Constructor with given configuration for the registry and its server context.
	 * This constructor will create a registry with a MongoDB backend.
	 * 
	 * @param contextConfig
	 *            The context configuration
	 * @param mongoDBConfig
	 *            The mongoDB configuration
	 */
	public RegistryComponent(BaSyxContextConfiguration contextConfig, BaSyxMongoDBConfiguration mongoDBConfig) {
		this.contextConfig = contextConfig;
		this.registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.MONGODB);
		this.mongoDBConfig = mongoDBConfig;
	}

	/**
	 * Constructor with given configuration for the registry and its server context.
	 * This constructor will create a registry with a MongoDB backend.
	 *
	 * @param contextConfig
	 *            The context configuration
	 * @param registryConfig
	 * @param mongoDBConfig
	 *            The mongoDB configuration
	 */
	public RegistryComponent(BaSyxContextConfiguration contextConfig, BaSyxRegistryConfiguration registryConfig, BaSyxMongoDBConfiguration mongoDBConfig) {
		this.contextConfig = contextConfig;
		this.registryConfig = registryConfig;
		this.mongoDBConfig = mongoDBConfig;
	}

	/**
	 * Constructor with given configuration for the registry and its server context.
	 * This constructor will create a registry with an SQL backend.
	 * 
	 * @param contextConfig
	 *            The context configuration
	 * @param sqlConfig
	 *            The sql configuration
	 */
	public RegistryComponent(BaSyxContextConfiguration contextConfig, BaSyxSQLConfiguration sqlConfig) {
		this.contextConfig = contextConfig;
		this.registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.SQL);
		this.sqlConfig = sqlConfig;
	}

	/**
	 * Constructor with given configuration for the registry and its server context.
	 * This constructor will create a registry with an SQL backend.
	 *
	 * @param contextConfig
	 *            The context configuration
	 * @param registryConfig
	 * @param sqlConfig
	 *            The sql configuration
	 */
	public RegistryComponent(BaSyxContextConfiguration contextConfig, BaSyxRegistryConfiguration registryConfig, BaSyxSQLConfiguration sqlConfig) {
		this.contextConfig = contextConfig;
		this.registryConfig = registryConfig;
		this.sqlConfig = sqlConfig;
	}

	/**
	 * Constructor with given configuration for the registry and its server context.
	 * Will load the backend configuration using the default load process.
	 * 
	 * @param contextConfig
	 *            The context configuration
	 * @param registryConfig
	 *            The registry configuration
	 */
	public RegistryComponent(BaSyxContextConfiguration contextConfig, BaSyxRegistryConfiguration registryConfig) {
		this.contextConfig = contextConfig;
		this.registryConfig = registryConfig;
	}

	/**
	 * Starts the context at http://${hostName}:${port}/${path}
	 */
	@Override
	public void startComponent() {
		loadRegistryFeaturesFromConfig();

		BaSyxContext context = contextConfig.createBaSyxContext();
		context.addServletMapping("/*", createRegistryServlet());
		if (registryConfig.isAuthorizationEnabled()) {
			configureContextForAuthorization(context);
		}

		server = new BaSyxHTTPServer(context);
		server.start();
		logger.info("Registry server started");
	}

	/**
	 * Sets and enables mqtt connection configuration for this component. Has to be
	 * called before the component is started.
	 * 
	 * @param configuration
	 */
	public void enableMQTT(BaSyxMqttConfiguration configuration) {
		this.mqttConfig = configuration;
	}

	/**
	 * Disables mqtt configuration. Has to be called before the component is
	 * started.
	 */
	public void disableMQTT() {
		this.mqttConfig = null;
	}

	private BaSyxSQLConfiguration loadSQLConfiguration() {
		BaSyxSQLConfiguration config;
		if (this.sqlConfig == null) {
			config = new BaSyxSQLConfiguration();
			config.loadFromDefaultSource();
		} else {
			config = this.sqlConfig;
		}
		return config;
	}

	private BaSyxMongoDBConfiguration loadMongoDBConfiguration() {
		BaSyxMongoDBConfiguration config;
		if (this.mongoDBConfig == null) {
			config = new BaSyxMongoDBConfiguration();
			config.loadFromDefaultSource();
		} else {
			config = this.mongoDBConfig;
		}
		return config;
	}

	private HttpServlet createRegistryServlet() {
		if (this.registryConfig.isTaggedDirectoryEnabled()) {
			return createTaggedRegistryServlet();
		}

		IAASRegistry registryBackend = createRegistryBackend();
		IAASRegistry decoratedRegistry = decorate(registryBackend);
		return new RegistryServlet(decoratedRegistry);
	}

	private HttpServlet createTaggedRegistryServlet() {
		throwRuntimeExceptionIfConfigurationIsNotSuitableForTaggedDirectory();
		logger.info("Enable tagged directory functionality");
		IAASTaggedDirectory taggedDirectory;
		if (registryConfig.getRegistryBackend().equals(RegistryBackend.MONGODB)) {
			taggedDirectory = new MongoDBTaggedDirectory(loadMongoDBConfiguration(), new HashMap<>());
		} else {
			taggedDirectory = new MapTaggedDirectory(new HashedMap<>(), new HashedMap<>());
		}
		IAASTaggedDirectory decoratedDirectory = decorateTaggedDirectory(taggedDirectory);
		return new TaggedDirectoryServlet(decoratedDirectory);
	}

	private IAASTaggedDirectory decorateTaggedDirectory(IAASTaggedDirectory taggedDirectory) {
		IAASTaggedDirectory decoratedTaggedDirectory = taggedDirectory;
		if (isMQTTEnabled()) {
			decoratedTaggedDirectory = configureMqttTagged(decoratedTaggedDirectory);
		}
		if (registryConfig.isAuthorizationEnabled()) {
			decoratedTaggedDirectory = decorateWithAuthorization(decoratedTaggedDirectory);
		}
		return decoratedTaggedDirectory;
	}

	private IAASTaggedDirectory configureMqttTagged(IAASTaggedDirectory decoratedTaggedDirectory) {
		logger.info("Enable MQTT events for broker " + this.mqttConfig.getServer());
		if (registryConfig.getRegistryEvents().equals(RegistryEventBackend.MQTT)) {
			decoratedTaggedDirectory = new MqttTaggedDirectoryFactory().create(decoratedTaggedDirectory, this.mqttConfig);
			logger.info("MQTT event backend for " + this.registryConfig.getRegistryId());
		} else if (registryConfig.getRegistryEvents().equals(RegistryEventBackend.MQTTV2)) {
			decoratedTaggedDirectory = new MqttV2TaggedDirectoryFactory().create(decoratedTaggedDirectory, mqttConfig, this.registryConfig, new Base64URLEncoder());
			logger.info("MQTTV2 event backend for " + this.registryConfig.getRegistryId());
		} else if (registryConfig.getRegistryEvents().equals(RegistryEventBackend.MQTTV2_SIMPLE_ENCODING)) {
			logger.info("MQTTV2_SIMPLE_ENCODING event backend for " + this.registryConfig.getRegistryId());
			decoratedTaggedDirectory = new MqttV2TaggedDirectoryFactory().create(decoratedTaggedDirectory, this.mqttConfig, this.registryConfig, new URLEncoder());
		}
		return decoratedTaggedDirectory;
	}

	private void throwRuntimeExceptionIfConfigurationIsNotSuitableForTaggedDirectory() {
		if (!isConfigurationSuitableForTaggedDirectory()) {
			throw new RuntimeException("The current version does not support this configuration.\n" + "\t* Persistent backend SQL\n" + "is currently not supported in combination with tagged directory functionality.");
		}
	}

	private IAASRegistry createRegistryBackend() {
		final RegistryBackend backendType = registryConfig.getRegistryBackend();
		switch (backendType) {
			case MONGODB:
				return createMongoDBRegistryBackend();
			case SQL:
				return createSQLRegistryBackend();
			case INMEMORY:
				return createInMemoryRegistryBackend();
			default:
				throw new RuntimeException("Unknown backend type " + backendType);
		}
	}

	private IAASRegistry createInMemoryRegistryBackend() {
		logger.info("Creating InMemoryRegistry");
		return new InMemoryRegistry();
	}

	private IAASRegistry createSQLRegistryBackend() {
		logger.info("Creating SQLRegistry");
		final BaSyxSQLConfiguration sqlConfiguration = loadSQLConfiguration();
		return new SQLRegistry(sqlConfiguration);
	}

	private IAASRegistry createMongoDBRegistryBackend() {
		logger.info("Creating MongoDBRegistry");
		final BaSyxMongoDBConfiguration mongoDBConfiguration = loadMongoDBConfiguration();
		return new MongoDBRegistry(mongoDBConfiguration);
	}

	private IAASRegistry decorate(IAASRegistry aasRegistry) {
		IAASRegistry decoratedRegistry = aasRegistry;

		if (isMQTTEnabled()) {
			decoratedRegistry = configureMqtt(decoratedRegistry);
		}

		if (registryConfig.isAuthorizationEnabled()) {
			decoratedRegistry = decorateWithAuthorization(decoratedRegistry);
		}

		return decoratedRegistry;
	}

	private SecurityFeature getSecurityFeature() {
		final String strategyString = securityConfig.getAuthorizationStrategy();

		if (strategyString == null) {
			throw new IllegalArgumentException(String.format("no authorization strategy set, please set %s in aas.properties", BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY));
		}

		AuthorizationStrategy strategy;
		try {
			strategy = AuthorizationStrategy.valueOf(strategyString);
		} catch (final IllegalArgumentException e) {
			throw new IllegalArgumentException(String.format("unknown authorization strategy %s set in aas.properties, available options: %s", strategyString, Arrays
					.toString(BaSyxSecurityConfiguration.AuthorizationStrategy.values())));
		}

		switch (strategy) {
			case SimpleRbac: {
				return new SimpleRbacSecurityFeature(securityConfig);
			}
			case GrantedAuthority: {
				return new GrantedAuthoritySecurityFeature(securityConfig);
			}
			case Custom: {
				return new SecurityFeature(securityConfig);
			}
			default:
				throw new UnsupportedOperationException("no handler for authorization strategy " + strategyString);
		}
	}

	private void configureContextForAuthorization(final BaSyxContext context) {
		final IJwtBearerTokenAuthenticationConfigurationProvider jwtBearerTokenAuthenticationConfigurationProvider = getJwtBearerTokenAuthenticationConfigurationProvider();
		if (jwtBearerTokenAuthenticationConfigurationProvider != null) {
			context.setJwtBearerTokenAuthenticationConfiguration(
					jwtBearerTokenAuthenticationConfigurationProvider.get(securityConfig)
			);
		}
	}

	private IJwtBearerTokenAuthenticationConfigurationProvider getJwtBearerTokenAuthenticationConfigurationProvider() {
		return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_JWT_BEARER_TOKEN_AUTHENTICATION_CONFIGURATION_PROVIDER, IJwtBearerTokenAuthenticationConfigurationProvider.class);
	}

	private IAASRegistry decorateWithAuthorization(IAASRegistry registry) {
		logger.info("Enable Authorization for Registry");
		return getSecurityFeature().getDecorator().decorateRegistry(registry);
	}

	private IAASTaggedDirectory decorateWithAuthorization(IAASTaggedDirectory taggedDirectory) {
		logger.info("Enable Authorization for TaggedDirectory");
		return getSecurityFeature().getDecorator().decorateTaggedDirectory(taggedDirectory);
	}

	private IAASRegistry configureMqtt(IAASRegistry decoratedRegistry) {
		logger.info("Enable MQTT events for broker " + this.mqttConfig.getServer());
		if (registryConfig.getRegistryEvents().equals(RegistryEventBackend.MQTT)) {
			decoratedRegistry = new MqttRegistryFactory().create(decoratedRegistry, this.mqttConfig);
			logger.info("MQTT event backend for " + this.registryConfig.getRegistryId());
		} else if (registryConfig.getRegistryEvents().equals(RegistryEventBackend.MQTTV2)) {
			decoratedRegistry = new MqttV2RegistryFactory().create(decoratedRegistry, this.mqttConfig, this.registryConfig, new Base64URLEncoder());
			logger.info("MQTTV2 event backend for " + this.registryConfig.getRegistryId());
		} else if (registryConfig.getRegistryEvents().equals(RegistryEventBackend.MQTTV2_SIMPLE_ENCODING)) {
			logger.info("MQTTV2_SIMPLE_ENCODING event backend for " + this.registryConfig.getRegistryId());
			decoratedRegistry = new MqttV2RegistryFactory().create(decoratedRegistry, this.mqttConfig, this.registryConfig, new URLEncoder());
		}
		return decoratedRegistry;
	}

	private boolean isMQTTEnabled() {
		return this.mqttConfig != null && !registryConfig.getRegistryEvents().equals(RegistryEventBackend.NONE);
	}

	private boolean isConfigurationSuitableForTaggedDirectory() {
		return !(registryConfig.getRegistryBackend().equals(RegistryBackend.SQL));
	}

	private void loadRegistryFeaturesFromConfig() {
		configureAuthorization();
	}

	private void configureAuthorization() {
		securityConfig = new BaSyxSecurityConfiguration();
		securityConfig.loadFromDefaultSource();
	}

	@Override
	public void stopComponent() {
		server.shutdown();
		logger.info("Registry server stopped");
	}
}
