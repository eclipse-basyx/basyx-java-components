/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.AASModelProvider;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.aascomponent.InMemoryAASServerComponentFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Referable;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import jakarta.persistence.EntityManager;

/**
 * An IAASAggregator for persistent storage in a MongoDB.
 *
 * @see AASAggregator AASAggregator for the "InMemory"-variant
 *
 * @author espen
 *
 */
public class MongoDBAASAggregator implements IAASAggregator {
	private static Logger logger = LoggerFactory.getLogger(MongoDBAASAggregator.class);

	private static final String IDSHORTPATH = Referable.IDSHORT;
	private static final String IDPATH = Identifiable.IDENTIFICATION + "." + Identifier.ID;

	protected Map<String, MultiSubmodelProvider> aasProviderMap = new HashMap<>();
	protected BaSyxMongoDBConfiguration config;
	protected MongoOperations mongoOps;
	protected String aasCollection;
	protected String smCollection;
	protected EntityManager entityManager;

	private IAASRegistry registry;

	/**
	 * Store AAS API Provider. By default, uses the MongoDB API Provider
	 */
	protected IAASAPIFactory aasApiProvider;

	/**
	 * Store Submodel API Provider. By default, uses a MongoDB Submodel Provider
	 */
	protected ISubmodelAPIFactory smApiProvider;

	/**
	 * Store SubmodelAggregator. By default, uses standard SubmodelAggregator
	 */
	protected ISubmodelAggregator submodelAggregator;

	/**
	 * Receives a BaSyxMongoDBConfiguration and a registry to create a persistent
	 * MongoDB backend.
	 *
	 * @param config
	 *            The MongoDB Configuration
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link InMemoryAASServerComponentFactory}.
	 */
	@Deprecated
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config) {
		this.setConfiguration(config);
		submodelAggregator = new SubmodelAggregator(smApiProvider);
		init();
	}

	/**
	 * Receives a BaSyxMongoDBConfiguration and a registry to create a persistent
	 * MongoDB backend.
	 *
	 * @param config
	 *            The MongoDB Configuration
	 * @param registry
	 *            The registry
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link InMemoryAASServerComponentFactory}.
	 */
	@Deprecated
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config, IAASRegistry registry) {
		this.setConfiguration(config);
		this.registry = registry;
		submodelAggregator = new SubmodelAggregator(smApiProvider);
		init();
	}

	/**
	 * Receives the path of the .properties file in it's constructor from a
	 * resource.
	 *
	 * @param resourceConfigPath
	 *            Path of the configuration file
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link InMemoryAASServerComponentFactory}
	 */
	@Deprecated
	public MongoDBAASAggregator(String resourceConfigPath) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		this.setConfiguration(config);
		submodelAggregator = new SubmodelAggregator(smApiProvider);
		init();
	}

	/**
	 * Receives the path of the .properties file from a resource and the registry in
	 * it's constructor.
	 *
	 * @param resourceConfigPath
	 *            Path of the configuration file
	 * @param registry
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link InMemoryAASServerComponentFactory}
	 */
	@Deprecated
	public MongoDBAASAggregator(String resourceConfigPath, IAASRegistry registry) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		this.setConfiguration(config);
		submodelAggregator = new SubmodelAggregator(smApiProvider);
		this.registry = registry;
		init();
	}

	/**
	 * Constructor using default connections
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link InMemoryAASServerComponentFactory}
	 */
	@Deprecated
	public MongoDBAASAggregator() {
		this(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH);
	}

	/**
	 * Constructor using default connections with registry as a parameter
	 *
	 * @param registry
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link InMemoryAASServerComponentFactory}
	 */
	@Deprecated
	public MongoDBAASAggregator(IAASRegistry registry) {
		this(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH, registry);
	}

	/**
	 * Receives a BaSyxMongoDBConfiguration, IAASRegistry, IAASAPIFactory and a
	 * ISubmodelAggregatorFactory to create a persistent MongoDB backend.
	 * 
	 * @param config
	 * @param registry
	 * @param aasAPIFactory
	 * @param submodelAggregatorFactory
	 * 
	 */
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config, IAASRegistry registry, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		setMongoDBConfiguration(config);
		this.config = config;
		this.registry = registry;
		this.aasApiProvider = aasAPIFactory;
		this.submodelAggregator = submodelAggregatorFactory.create();
		init();
	}

	/**
	 * Receives a BaSyxMongoDBConfiguration, IAASAPIFactory and a
	 * ISubmodelAggregatorFactory to create a persistent MongoDB backend.
	 * 
	 * @param config
	 * @param aasAPIFactory
	 * @param submodelAggregatorFactory
	 * 
	 */
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		setMongoDBConfiguration(config);
		this.config = config;
		this.aasApiProvider = aasAPIFactory;
		this.submodelAggregator = submodelAggregatorFactory.create();
		init();
	}

	/**
	 * Receives a resourceConfigPath, IAASRegistry, IAASAPIFactory and a
	 * ISubmodelAggregatorFactory to create a persistent MongoDB backend.
	 * 
	 * @param resourceConfigPath
	 * @param registry
	 * @param aasAPIFactory
	 * @param submodelAggregatorFactory
	 * 
	 */
	public MongoDBAASAggregator(String resourceConfigPath, IAASRegistry registry, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		setMongoDBConfiguration(config);
		this.registry = registry;
		this.aasApiProvider = aasAPIFactory;
		this.submodelAggregator = submodelAggregatorFactory.create();
		init();
	}

	/**
	 * Receives a resourceConfigPath, IAASAPIFactory and a
	 * ISubmodelAggregatorFactory to create a persistent MongoDB backend.
	 * 
	 * @param resourceConfigPath
	 * @param aasAPIFactory
	 * @param submodelAggregatorFactory
	 * 
	 */
	public MongoDBAASAggregator(String resourceConfigPath, IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		setMongoDBConfiguration(config);
		this.aasApiProvider = aasAPIFactory;
		this.submodelAggregator = submodelAggregatorFactory.create();
		init();
	}

	/**
	 * Constructor using the default configuration, with the given IAASAPIFactory
	 * and ISubmodelAggregatorFactory.
	 * 
	 * @param aasAPIFactory
	 * @param submodelAggregatorFactory
	 * 
	 */
	public MongoDBAASAggregator(IAASAPIFactory aasAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH, aasAPIFactory, submodelAggregatorFactory);
	}

	/**
	 * Sets the IAASRegistry instance.
	 *
	 * @deprecated This method is deprecated due to a bug. Use constructors
	 *             {@link #MongoDBAASAggregator(IAASRegistry) or
	 *             #MongoDBAASAggregator(BaSyxMongoDBConfiguration, IAASRegistry) or
	 *             #MongoDBAASAggregator(String, IAASRegistry) } to set the
	 *             IAASRegistry instance.
	 *
	 * @param registry
	 *            Asset Adminstration Shell's Registry
	 */
	@Deprecated
	public void setRegistry(IAASRegistry registry) {
		this.registry = registry;
	}

	/**
	 * Sets the db configuration for this Aggregator.
	 *
	 * @param config
	 *            The MongoDB Configuration
	 * 
	 * @deprecated This method is used with the old, deprecated Constructors. Use
	 *             {@link InMemoryAASServerComponentFactory} instead
	 */
	@Deprecated
	public void setConfiguration(BaSyxMongoDBConfiguration config) {
		// set mongoDB configuration
		this.config = config;
		MongoClient client = MongoClients.create(config.getConnectionUrl());
		this.mongoOps = new MongoTemplate(client, config.getDatabase());
		this.aasCollection = config.getAASCollection();
		this.smCollection = config.getSubmodelCollection();

		// Create API factories with the given configuration
		this.aasApiProvider = aas -> {
			MongoDBAASAPI api = new MongoDBAASAPI(config, aas.getIdentification().getId());
			api.setAAS(aas);
			return api;
		};
		if (this.entityManager == null) {
			this.smApiProvider = sm -> {
				MongoDBSubmodelAPI api = new MongoDBSubmodelAPI(config, sm.getIdentification().getId());
				api.setSubmodel(sm);
				return api;
			};
		} else {
			this.smApiProvider = sm -> {
				MongoDBSubmodelAPI api = new StorageMongoDBSubmodelAPI(config, sm.getIdentification().getId());
				api.setSubmodel(sm);
				return api;
			};
		}
	}

	private void setMongoDBConfiguration(BaSyxMongoDBConfiguration config) {
		this.config = config;
		MongoClient client = MongoClients.create(config.getConnectionUrl());
		this.mongoOps = new MongoTemplate(client, config.getDatabase());
		this.aasCollection = config.getAASCollection();
		this.smCollection = config.getSubmodelCollection();
	}

	/**
	 * Removes all persistent AAS and submodels
	 */
	public void reset() {
		mongoOps.dropCollection(aasCollection);
		mongoOps.dropCollection(smCollection);
		aasProviderMap.clear();
	}

	private void init() {
		List<AssetAdministrationShell> data = mongoOps.findAll(AssetAdministrationShell.class, aasCollection);
		for (AssetAdministrationShell aas : data) {
			String aasId = aas.getIdentification().getId();
			logger.info("Adding AAS from DB: " + aasId);
			MongoDBAASAPI aasApi = new MongoDBAASAPI(config, aasId);
			MultiSubmodelProvider provider = createMultiSubmodelProvider(aasApi);
			addSubmodelsFromDB(provider, aas);
			aasProviderMap.put(aas.getIdentification().getId(), provider);
		}
	}

	/**
	 * Initializes and returns a VABMultiSubmodelProvider with only the
	 * AssetAdministrationShell
	 */
	private MultiSubmodelProvider createMultiSubmodelProvider(IAASAPI aasApi) {
		AASModelProvider aasProvider = new AASModelProvider(aasApi);
		IConnectorFactory connProvider = new HTTPConnectorFactory();
		return new MultiSubmodelProvider(aasProvider, registry, connProvider, aasApiProvider, submodelAggregator);
	}

	/**
	 * Adds submodel providers for submodels in the MongoDB
	 */
	private void addSubmodelsFromDB(MultiSubmodelProvider provider, AssetAdministrationShell aas) {
		// Get ids and idShorts from aas
		Collection<IReference> submodelRefs = aas.getSubmodelReferences();
		List<String> smIds = new ArrayList<>();
		List<String> smIdShorts = new ArrayList<>();
		for (IReference ref : submodelRefs) {
			List<IKey> keys = ref.getKeys();
			IKey lastKey = keys.get(keys.size() - 1);
			if (lastKey.getIdType() == KeyType.IDSHORT) {
				smIdShorts.add(lastKey.getValue());
			} else {
				smIds.add(lastKey.getValue());
			}
		}

		// Add submodel ids by id shorts
		for (String idShort : smIdShorts) {
			String id = getSubmodelId(idShort);
			if (id != null) {
				smIds.add(id);
			}
		}

		// Create a provider for each submodel
		for (String id : smIds) {
			logger.info("Adding Submodel from DB: " + id);
			addSubmodelProvidersById(id, provider);
		}
	}

	private String getSubmodelId(String idShort) {
		Submodel sm = mongoOps.findOne(query(where(IDSHORTPATH).is(idShort)), Submodel.class);
		if (sm != null) {
			return sm.getIdentification().getId();
		}
		return null;
	}

	private void addSubmodelProvidersById(String smId, MultiSubmodelProvider provider) {
		ISubmodelAPI smApi;
		if (this.entityManager == null) {
			smApi = new MongoDBSubmodelAPI(config, smId);
		} else {
			smApi = new StorageMongoDBSubmodelAPI(config, smId);
		}
		SubmodelProvider smProvider = new SubmodelProvider(smApi);
		provider.addSubmodel(smProvider);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return aasProviderMap.values().stream().map(p -> {
			try {
				return p.getValue("/aas");
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new RuntimeException();
			}
		}).map(m -> {
			AssetAdministrationShell aas = new AssetAdministrationShell();
			aas.putAll((Map<? extends String, ? extends Object>) m);
			return aas;
		}).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public IAssetAdministrationShell getAAS(IIdentifier aasId) {
		IModelProvider aasProvider = getAASProvider(aasId);

		// get all Elements from provider
		Map<String, Object> aasMap = (Map<String, Object>) aasProvider.getValue("/aas");
		return AssetAdministrationShell.createAsFacade(aasMap);
	}

	@Override
	public void createAAS(AssetAdministrationShell aas) {
		IAASAPI aasApi = this.aasApiProvider.create(aas);
		MultiSubmodelProvider provider = createMultiSubmodelProvider(aasApi);
		aasProviderMap.put(aas.getIdentification().getId(), provider);
	}

	@Override
	public void updateAAS(AssetAdministrationShell aas) {
		MultiSubmodelProvider oldProvider = (MultiSubmodelProvider) getAASProvider(aas.getIdentification());
		IAASAPI aasApi = aasApiProvider.create(aas);
		AASModelProvider contentProvider = new AASModelProvider(aasApi);
		IConnectorFactory connectorFactory = oldProvider.getConnectorFactory();

		MultiSubmodelProvider updatedProvider = new MultiSubmodelProvider(contentProvider, registry, connectorFactory, aasApiProvider, oldProvider.getSmAggregator());

		aasProviderMap.put(aas.getIdentification().getId(), updatedProvider);
	}

	@Override
	public void deleteAAS(IIdentifier aasId) {
		Query hasId = query(where(IDPATH).is(aasId.getId()));
		mongoOps.remove(hasId, aasCollection);
		aasProviderMap.remove(aasId.getId());
	}

	public MultiSubmodelProvider getProviderForAASId(String aasId) {
		return aasProviderMap.get(aasId);
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier aasId) {
		MultiSubmodelProvider provider = aasProviderMap.get(aasId.getId());

		if (provider == null) {
			throw new ResourceNotFoundException("AAS with Id " + aasId.getId() + " does not exist");
		}

		return provider;
	}
}
