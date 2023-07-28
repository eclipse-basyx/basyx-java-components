/*******************************************************************************
 * Copyright (C) 2021, 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.components.aas.mongodb;

import java.util.Collection;
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
import org.eclipse.basyx.components.aas.aascomponent.MongoDBAASServerComponentFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPIFactory;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * An IAASAggregator for persistent storage in a MongoDB.
 *
 * @see AASAggregator AASAggregator for the "InMemory"-variant
 *
 * @author espen, wege, witt, jugnjan, zhangzai
 *
 */
public class MongoDBAASAggregator implements IAASAggregator {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private IAASRegistry registry;

	/**
	 * Store AAS API Provider. By default, uses the MongoDB API Provider
	 */
	protected IAASAPIFactory shellApiFactory;

	/**
	 * Store Submodel API Provider. By default, uses a MongoDB Submodel Provider
	 */
	protected ISubmodelAPIFactory submodelApiFactory;

	/**
	 * Store SubmodelAggregator. By default, uses standard SubmodelAggregator
	 * 
	 * @deprecated Please use {@link #submodelAggregatorFactory}
	 */
	@Deprecated
	protected ISubmodelAggregator submodelAggregator;
	protected ISubmodelAggregatorFactory submodelAggregatorFactory;

	private MongoDBBaSyxStorageAPI<Submodel> submodelStorageApi;
	private MongoDBBaSyxStorageAPI<AssetAdministrationShell> shellStorageApi;

	public MongoDBAASAggregator(IAASRegistry registry, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoDBBaSyxStorageAPI<Submodel> submodelStorageApi,
			MongoDBBaSyxStorageAPI<AssetAdministrationShell> shellStorageApi) {
		this.submodelStorageApi = submodelStorageApi;
		this.shellStorageApi = shellStorageApi;
		this.shellApiFactory = shellAPIFactory;
		this.submodelAggregatorFactory = submodelAggregatorFactory;
		this.registry = registry;
	}

	/**
	 * Receives a BaSyxMongoDBConfiguration, IAASRegistry, IAASAPIFactory,
	 * ISubmodelAggregatorFactory and a MongoClient to create a persistent MongoDB
	 * backend.
	 * 
	 * @param config
	 * @param registry
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * 
	 */
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config, IAASRegistry registry, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this(registry, shellAPIFactory, submodelAggregatorFactory, submodelStorageApiFromConfig(config, client), shellStorageApiFromConfig(config, client));
	}

	/**
	 * Receives a BaSyxMongoDBConfiguration,
	 * IAASAPIFactory,ISubmodelAggregatorFactory and a MongoClient to create a
	 * persistent MongoDB backend.
	 * 
	 * @param config
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @param client
	 * 
	 */
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this(config, null, shellAPIFactory, submodelAggregatorFactory, client);
	}

	/**
	 * Receives a resourceConfigPath, IAASRegistry, IAASAPIFactory,
	 * ISubmodelAggregatorFactory and a MongoClient to create a persistent MongoDB
	 * backend.
	 * 
	 * @param resourceConfigPath
	 * @param registry
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @param client
	 *            Use the new constructor using a MongoClient
	 * 
	 */
	public MongoDBAASAggregator(String resourceConfigPath, IAASRegistry registry, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this(loadConfigFromPath(resourceConfigPath), registry, shellAPIFactory, submodelAggregatorFactory, client);
	}

	/**
	 * Receives a resourceConfigPath, IAASAPIFactory, ISubmodelAggregatorFactory and
	 * a MongoClient to create a persistent MongoDB backend.
	 * 
	 * @param resourceConfigPath
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @param client
	 * 
	 */
	public MongoDBAASAggregator(String resourceConfigPath, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this(loadConfigFromPath(resourceConfigPath), shellAPIFactory, submodelAggregatorFactory, client);
	}

	/**
	 * Constructor using the default configuration, with the given
	 * IAASAPIFactory,ISubmodelAggregatorFactory and MongoClient.
	 * 
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @param client
	 */
	public MongoDBAASAggregator(IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory, MongoClient client) {
		this(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH, shellAPIFactory, submodelAggregatorFactory, client);
	}

	/**
	 * Receives a BaSyxMongoDBConfiguration and a registry to create a persistent
	 * MongoDB backend.
	 *
	 * @param config
	 *            The MongoDB Configuration
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link MongoDBAASServerComponentFactory}.
	 */
	@Deprecated
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config) {
		this(config, null);
	}

	/**
	 * Receives the path of the .properties file in it's constructor from a
	 * resource.
	 *
	 * @param resourceConfigPath
	 *            Path of the configuration file
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link MongoDBAASServerComponentFactory}
	 */
	@Deprecated
	public MongoDBAASAggregator(String resourceConfigPath) {
		this(loadConfigFromPath(resourceConfigPath));
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
	 *             {@link MongoDBAASServerComponentFactory}
	 */
	@Deprecated
	public MongoDBAASAggregator(String resourceConfigPath, IAASRegistry registry) {
		this(loadConfigFromPath(resourceConfigPath), registry);
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
	 *             {@link MongoDBAASServerComponentFactory}.
	 */
	@Deprecated
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config, IAASRegistry registry) {
		this(registry, initShellApiFactory(config), initSubmodelAggregatorFactory(config), submodelStorageApiFromConfig(config, null), shellStorageApiFromConfig(config, null));
	}

	/**
	 * Constructor using default connections
	 * 
	 * @deprecated Use new MongoDBAASAggregator with the
	 *             {@link MongoDBAASServerComponentFactory}
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
	 *             {@link MongoDBAASServerComponentFactory}
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
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config, IAASRegistry registry, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(config, registry, shellAPIFactory, submodelAggregatorFactory, MongoClients.create(config.getConnectionUrl()));
	}

	/**
	 * Receives a BaSyxMongoDBConfiguration, IAASAPIFactory and a
	 * ISubmodelAggregatorFactory to create a persistent MongoDB backend.
	 * 
	 * @param config
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBAASAggregator(BaSyxMongoDBConfiguration config, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(config, shellAPIFactory, submodelAggregatorFactory, MongoClients.create(config.getConnectionUrl()));
	}

	/**
	 * Receives a resourceConfigPath, IAASRegistry, IAASAPIFactory and a
	 * ISubmodelAggregatorFactory to create a persistent MongoDB backend.
	 * 
	 * @param resourceConfigPath
	 * @param registry
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBAASAggregator(String resourceConfigPath, IAASRegistry registry, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(loadConfigFromPath(resourceConfigPath), registry, shellAPIFactory, submodelAggregatorFactory);
	}

	/**
	 * Receives a resourceConfigPath, IAASAPIFactory and a
	 * ISubmodelAggregatorFactory to create a persistent MongoDB backend.
	 * 
	 * @param resourceConfigPath
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBAASAggregator(String resourceConfigPath, IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(loadConfigFromPath(resourceConfigPath), shellAPIFactory, submodelAggregatorFactory);
	}

	/**
	 * Constructor using the default configuration, with the given IAASAPIFactory
	 * and ISubmodelAggregatorFactory.
	 * 
	 * @param shellAPIFactory
	 * @param submodelAggregatorFactory
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBAASAggregator(IAASAPIFactory shellAPIFactory, ISubmodelAggregatorFactory submodelAggregatorFactory) {
		this(BaSyxMongoDBConfiguration.DEFAULT_CONFIG_PATH, shellAPIFactory, submodelAggregatorFactory);
	}

	private static ISubmodelAggregatorFactory initSubmodelAggregatorFactory(BaSyxMongoDBConfiguration config) {
		ISubmodelAPIFactory submodelApiFactory = initSubmodelApiFactory(config);
		return new SubmodelAggregatorFactory(submodelApiFactory);
	}

	private static ISubmodelAPIFactory initSubmodelApiFactory(BaSyxMongoDBConfiguration config) {
		return new MongoDBSubmodelAPIFactory(config);
	}

	private MongoDBSubmodelAPI createAPI(Submodel submodel, BaSyxMongoDBConfiguration config) {
		MongoDBSubmodelAPI api = new MongoDBSubmodelAPI(config, submodel.getIdentification().getId());
		api.setSubmodel(submodel);
		return api;
	}

	private static IAASAPIFactory initShellApiFactory(BaSyxMongoDBConfiguration config) {
		return new MongoDBAASAPIFactory(config);
	}

	private static MongoDBBaSyxStorageAPI<Submodel> submodelStorageApiFromConfig(BaSyxMongoDBConfiguration config, MongoClient client) {
		String submodelCollectionName = config.getSubmodelCollection();
		MongoDBBaSyxStorageAPI<Submodel> submodelStorageApi = client == null ? MongoDBBaSyxStorageAPIFactory.<Submodel>create(submodelCollectionName, Submodel.class, config)
				: MongoDBBaSyxStorageAPIFactory.<Submodel>create(submodelCollectionName, Submodel.class, config, client);
		return submodelStorageApi;
	}

	private static MongoDBBaSyxStorageAPI<AssetAdministrationShell> shellStorageApiFromConfig(BaSyxMongoDBConfiguration config, MongoClient client) {
		String shellCollectionName = config.getAASCollection();
		MongoDBBaSyxStorageAPI<AssetAdministrationShell> shellStorageApi = client == null ? MongoDBBaSyxStorageAPIFactory.<AssetAdministrationShell>create(shellCollectionName, AssetAdministrationShell.class, config)
				: MongoDBBaSyxStorageAPIFactory.<AssetAdministrationShell>create(shellCollectionName, AssetAdministrationShell.class, config, client);
		return shellStorageApi;
	}

	private static BaSyxMongoDBConfiguration loadConfigFromPath(String resourceConfigPath) {
		BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		return config;
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
	 * Removes all persistent AAS and submodels
	 */
	public void reset() {
		Collection<AssetAdministrationShell> shells = shellStorageApi.retrieveAll();
		Collection<Submodel> submodels = submodelStorageApi.retrieveAll();
		shells.forEach(shell -> shellStorageApi.delete(shell.getIdentification().getId()));
		submodels.forEach(shell -> submodelStorageApi.delete(shell.getIdentification().getId()));
	}

	/**
	 * Initializes and returns a VABMultiSubmodelProvider with only the
	 * AssetAdministrationShell
	 */
	private MultiSubmodelProvider createMultiSubmodelProvider(IAASAPI shellApi) {
		AASModelProvider contentProvider = createContentProvider(shellApi);
		IConnectorFactory connectorFactory = new HTTPConnectorFactory();

		ISubmodelAggregator submodelAggregator = getSubmodelAggregatorInstance();

		return new MultiSubmodelProvider(contentProvider, this.registry, connectorFactory, this.shellApiFactory, submodelAggregator);
	}

	private AASModelProvider createContentProvider(IAASAPI shellApi) {
		return new AASModelProvider(shellApi);
	}

	private ISubmodelAggregator getSubmodelAggregatorInstance() {
		if (submodelAggregatorFactory == null) {
			return submodelAggregator;
		}

		return submodelAggregatorFactory.create();
	}

	/**
	 * Adds submodel providers for submodels in the MongoDB
	 */
	private void addSubmodelsFromDB(MultiSubmodelProvider provider, AssetAdministrationShell shell) {
		Collection<IReference> submodelRefs = shell.getSubmodelReferences();
		List<String> submodelIdentificationIds = getSubmodelIdentificationIdsFromSubmodelReferences(submodelRefs);
		List<String> submodelIdShorts = getSubmodelIdShortsFromSubmodelReferences(submodelRefs);
		submodelIdentificationIds = completeSubmodelIdentificationsIdsByIdShorts(submodelIdentificationIds, submodelIdShorts);

			createProviderForSubmodels(provider, submodelIdentificationIds);

	}

	private void createProviderForSubmodels(MultiSubmodelProvider provider, List<String> submodelIdentificationIds) {
		submodelIdentificationIds.forEach(submodelIdentificationId -> addSubmodelProvidersById(submodelIdentificationId, provider));
	}

	private List<String> completeSubmodelIdentificationsIdsByIdShorts(List<String> submodelIdentificationIds, List<String> submodelIdShorts) {
		submodelIdShorts.forEach(idShort -> {
			String id = getSubmodelIdByIdShort(idShort);
			if (id != null) {
				submodelIdentificationIds.add(id);
			}
		});
		return submodelIdentificationIds;
	}

	private List<String> getSubmodelIdentificationIdsFromSubmodelReferences(Collection<IReference> submodelRefs) {
		List<String> submodelIdentificationIds = submodelRefs.stream().map(this::getLastKeyFromReference).filter(lastKey -> lastKey.getIdType() != KeyType.IDSHORT).map(lastKey -> lastKey.getValue()).collect(Collectors.toList());
		return submodelIdentificationIds;
	}

	private List<String> getSubmodelIdShortsFromSubmodelReferences(Collection<IReference> submodelRefs) {
		List<String> submodelIdShorts = submodelRefs.stream().map(this::getLastKeyFromReference).filter(lastKey -> lastKey.getIdType() == KeyType.IDSHORT).map(lastKey -> lastKey.getValue()).collect(Collectors.toList());
		return submodelIdShorts;
	}

	private IKey getLastKeyFromReference(IReference reference) {
		List<IKey> keys = reference.getKeys();
		IKey lastKey = keys.get(keys.size() - 1);
		return lastKey;
	}

	private String getSubmodelIdByIdShort(String idShort) {
		Submodel submodel = submodelStorageApi.retrieve(idShort);
		if (submodel != null) {
			return submodel.getIdentification().getId();
		}
		return null;
	}

	private void addSubmodelProvidersById(String submodelIdentificationId, MultiSubmodelProvider provider) {
		ISubmodelAPI submodelApi = new MongoDBSubmodelAPI(this.submodelStorageApi, submodelIdentificationId);
		try {
			SubmodelProvider submodelProvider = new SubmodelProvider(submodelApi);
			provider.addSubmodel(submodelProvider);
		} catch (ResourceNotFoundException noSubmodelsInDB) {
			// ignore
			logger.warn("Could not add submodel with identificationId '{}'.", submodelIdentificationId);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IAssetAdministrationShell> getAASList() {
		return shellStorageApi.retrieveAll().stream().map(aas -> (IAssetAdministrationShell) aas).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public IAssetAdministrationShell getAAS(IIdentifier shellIdentification) {
		IModelProvider shellProvider = getAASProvider(shellIdentification);

		Map<String, Object> shellMap = (Map<String, Object>) shellProvider.getValue("/aas");
		return AssetAdministrationShell.createAsFacade(shellMap);
	}

	@Override
	public void createAAS(AssetAdministrationShell shell) {
		this.shellApiFactory.create(shell);
	}

	@Override
	public void updateAAS(AssetAdministrationShell shell) {
		this.shellApiFactory.create(shell);
	}

	private MultiSubmodelProvider updateAASProvider(IAASAPI shellApi, MultiSubmodelProvider oldProvider) {
		AASModelProvider contentProvider = createContentProvider(shellApi);
		IConnectorFactory connectorFactory = oldProvider.getConnectorFactory();
		ISubmodelAggregator submodelAggregator = oldProvider.getSmAggregator();

		return new MultiSubmodelProvider(contentProvider, this.registry, connectorFactory, shellApiFactory, submodelAggregator);
	}

	@Override
	public void deleteAAS(IIdentifier shellIdentifier) {
		String shellIdentificationId = shellIdentifier.getId();
		shellStorageApi.delete(shellIdentificationId);
	}

	public MultiSubmodelProvider getProviderForAASId(String shellIdentificationId) {
		AssetAdministrationShell shell = shellStorageApi.retrieve(shellIdentificationId);

		IAASAPI shellApi = this.shellApiFactory.create(shell); // FIXME a new shell is created every time when using shellApiFactory
		MultiSubmodelProvider provider = createMultiSubmodelProvider(shellApi);

		addSubmodelsFromDB(provider, shell);
		return provider;
	}

	@Override
	public IModelProvider getAASProvider(IIdentifier shellIdentificationId) {
		return getProviderForAASId(shellIdentificationId.getId());
	}
}
