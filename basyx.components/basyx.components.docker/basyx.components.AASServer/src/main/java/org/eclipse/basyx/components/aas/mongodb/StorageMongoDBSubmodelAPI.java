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

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.extensions.submodel.storage.SubmodelElementStorageComponent;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.restapi.operation.DelegatedInvocationManager;

import jakarta.persistence.EntityManager;

/**
 * Implements the ISubmodelAPI for a mongoDB backend.
 *
 * @author espen
 */
public class StorageMongoDBSubmodelAPI extends MongoDBSubmodelAPI {
	protected EntityManager entityManager;
	protected SubmodelElementStorageComponent submodelElementStorageComponent;

	/**
	 * Receives the path of the configuration.properties file in it's constructor.
	 *
	 * @param config
	 */
	public StorageMongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String smId) {
		super(config, smId);
	}

	public StorageMongoDBSubmodelAPI(BaSyxMongoDBConfiguration config, String smId, DelegatedInvocationManager invocationHelper) {
		super(config, smId, invocationHelper);
	}

	/**
	 * Receives the path of the .properties file in it's constructor from a
	 * resource.
	 */
	public StorageMongoDBSubmodelAPI(String resourceConfigPath, String smId) {
		super(resourceConfigPath, smId);
	}

	public StorageMongoDBSubmodelAPI(String resourceConfigPath, String smId, DelegatedInvocationManager invocationHelper) {
		super(resourceConfigPath, smId, invocationHelper);
	}

	/**
	 * Constructor using default sql connections
	 */
	public StorageMongoDBSubmodelAPI(String smId) {
		super(smId);
	}

	public StorageMongoDBSubmodelAPI(String smId, DelegatedInvocationManager invocationHelper) {
		super(smId, invocationHelper);
	}

	@Override
	public void addSubmodelElement(ISubmodelElement elem) {
		submodelElementStorageComponent.beginTransaction();
		submodelElementStorageComponent.persistStorageElementCreation(getSubmodel(), elem.getIdShort(), elem);
		super.addSubmodelElement(elem);
		submodelElementStorageComponent.commitTransaction();
	}

	@Override
	public void addSubmodelElement(String idShortPath, ISubmodelElement elem) {
		submodelElementStorageComponent.beginTransaction();
		submodelElementStorageComponent.persistStorageElementCreation(getSubmodel(), idShortPath, elem);
		super.addSubmodelElement(idShortPath, elem);
		submodelElementStorageComponent.commitTransaction();
	}

	@Override
	public void deleteSubmodelElement(String idShortPath) {
		submodelElementStorageComponent.beginTransaction();
		submodelElementStorageComponent.persistStorageElementDeletion(getSubmodel(), idShortPath);
		super.deleteSubmodelElement(idShortPath);
		submodelElementStorageComponent.commitTransaction();
	}

	@Override
	public void updateSubmodelElement(String idShortPath, Object newValue) {
		submodelElementStorageComponent.beginTransaction();
		submodelElementStorageComponent.persistStorageElementUpdate(getSubmodel(), idShortPath, newValue);
		super.updateSubmodelElement(idShortPath, newValue);
		submodelElementStorageComponent.commitTransaction();
	}

}
