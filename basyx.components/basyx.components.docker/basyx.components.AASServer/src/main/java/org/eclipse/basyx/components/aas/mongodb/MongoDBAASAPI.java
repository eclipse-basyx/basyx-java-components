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
import java.util.Optional;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoClient;

/**
 * Implements the IAASAPI for a mongoDB backend.
 * 
 * @author espen, jungjan, witt
 */
public class MongoDBAASAPI implements IAASAPI {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private static final String DEFAULT_CONFIG_PATH = "mongodb.properties";

	protected String collectionName;
	private MongoDBBaSyxStorageAPI<AssetAdministrationShell> storageApi;
	private String shellIdentificationId;

	/**
	 * Receives the path of the configuration.properties file in its constructor.
	 * 
	 * @param config
	 */
	public MongoDBAASAPI(BaSyxMongoDBConfiguration config, String shellIdentificationId, MongoClient client) {
		this(MongoDBBaSyxStorageAPIFactory.<AssetAdministrationShell>create(config.getAASCollection(), AssetAdministrationShell.class, config, client), shellIdentificationId);
	}

	public MongoDBAASAPI(MongoDBBaSyxStorageAPI<AssetAdministrationShell> mongoDBStorageAPI, String shellIdentificationId) {
		super();
		this.storageApi = mongoDBStorageAPI;
		this.shellIdentificationId = shellIdentificationId;
	}

	/**
	 * Receives the path of the .properties file in its constructor from a resource.
	 */
	public MongoDBAASAPI(String resourceConfigPath, String shellIdentificationId, MongoClient client) {
		this(MongoDBBaSyxStorageAPIFactory.<AssetAdministrationShell>create(configFromResource(resourceConfigPath).getSubmodelCollection(), AssetAdministrationShell.class, configFromResource(resourceConfigPath), client), shellIdentificationId);
	}

	/**
	 * Constructor using default MongoDB connections
	 */
	public MongoDBAASAPI(String shellIdentificationId, MongoClient client) {
		this(DEFAULT_CONFIG_PATH, shellIdentificationId, client);
	}

	/**
	 * Receives the path of the configuration.properties file in its constructor.
	 * 
	 * @param config
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBAASAPI(BaSyxMongoDBConfiguration config, String shellIdentificationId) {
		this(MongoDBBaSyxStorageAPIFactory.<AssetAdministrationShell>create(config.getAASCollection(), AssetAdministrationShell.class, config), shellIdentificationId);
	}

	/**
	 * Receives the path of the .properties file in its constructor from a resource.
	 * 
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBAASAPI(String resourceConfigPath, String shellIdentificationId) {
		this(MongoDBBaSyxStorageAPIFactory.<AssetAdministrationShell>create(configFromResource(resourceConfigPath).getSubmodelCollection(), AssetAdministrationShell.class, configFromResource(resourceConfigPath)), shellIdentificationId);
	}

	/**
	 * Constructor using default MongoDB connections
	 * 
	 * @deprecated Use the new constructor using a MongoClient
	 */
	@Deprecated
	public MongoDBAASAPI(String shellIdentificationId) {
		this(DEFAULT_CONFIG_PATH, shellIdentificationId);
	}

	private static BaSyxMongoDBConfiguration configFromResource(String resourceConfigPath) {
		BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
		config.loadFromResource(resourceConfigPath);
		return config;
	}

	/**
	 * This Method enables to switch the BaSyxMongoDBConfiguration at runtime
	 * 
	 * @param config
	 */
	public void setConfiguration(BaSyxMongoDBConfiguration config) {
		this.collectionName = config.getAASCollection();
		MongoDBBaSyxStorageAPIFactory<AssetAdministrationShell> storageApiFactory = new MongoDBBaSyxStorageAPIFactory<>(config, AssetAdministrationShell.class, this.collectionName);
		this.storageApi = storageApiFactory.create();
	}

	/**
	 * This Method enables to switch the BaSyxMongoDBConfiguration at runtime
	 * 
	 * @param config
	 */
	public void setConfiguration(BaSyxMongoDBConfiguration config, MongoClient client) {
		this.collectionName = config.getAASCollection();
		MongoDBBaSyxStorageAPIFactory<AssetAdministrationShell> storageApiFactory = new MongoDBBaSyxStorageAPIFactory<>(config, AssetAdministrationShell.class, this.collectionName, client);
		this.storageApi = storageApiFactory.create();
	}

	/**
	 * Sets the shellIdentificationId, so that this API points to the shell with
	 * shellIdentificationId. Can be changed to point to a different shell in the
	 * database.
	 * 
	 * @param shellIdentificationId
	 */
	public void setAASId(String shellIdentificationId) {
		this.shellIdentificationId = shellIdentificationId;
	}

	/**
	 * Depending on whether the model is already in the db, this method inserts or
	 * replaces the existing data. The new shell id for this API is taken from the
	 * given shell.
	 * 
	 * @param shell
	 */
	public void setAAS(AssetAdministrationShell shell) {
		String id = shell.getIdentification().getId();
		this.setAASId(id);
		storageApi.createOrUpdate(shell);
	}

	@Override
	public IAssetAdministrationShell getAAS() {
		return storageApi.retrieve(shellIdentificationId);
	}

	@Override
	public void addSubmodel(IReference submodelReference) {
		AssetAdministrationShell shell = (AssetAdministrationShell) getAAS();
		shell.addSubmodelReference(submodelReference);
		storageApi.update(shell, shellIdentificationId);
	}

	@Override
	public void removeSubmodel(String submodelIdShort) {
		AssetAdministrationShell shell = (AssetAdministrationShell) this.getAAS();
		Collection<IReference> submodelReferences = shell.getSubmodelReferences();

		Optional<IReference> toBeRemoved = submodelReferences.stream().filter(submodelReference -> getLastSubmodelReferenceKey(submodelReference).getValue().equals(submodelIdShort)).findFirst();
		if (!toBeRemoved.isPresent() || toBeRemoved.isEmpty()) {
			logger.warn("Submodel reference could not be removed. Shell with identification id '{}' does not contain submodel with idShort '{}'.", shell.getIdentification().getId(), submodelIdShort);
			return;
		}
		submodelReferences.remove(toBeRemoved.get());
		shell.setSubmodelReferences(submodelReferences);
		storageApi.update(shell, submodelIdShort);
	}

	private IKey getLastSubmodelReferenceKey(IReference submodelReference) {
		return submodelReference.getKeys().get(getLastSubmodelReferenceReferenceIndex(submodelReference));
	}

	private int getLastSubmodelReferenceReferenceIndex(IReference submodelReference) {
		return submodelReference.getKeys().size() - 1;
	}
}
