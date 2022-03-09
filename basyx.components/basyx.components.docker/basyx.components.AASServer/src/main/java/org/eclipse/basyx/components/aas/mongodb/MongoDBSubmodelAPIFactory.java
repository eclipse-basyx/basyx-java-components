/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.mongodb;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

/**
 * 
 * Factory for creating a MongoDBSubmodelAPI
 * 
 * @author fried
 *
 */
public class MongoDBSubmodelAPIFactory implements ISubmodelAPIFactory {

	private BaSyxMongoDBConfiguration config;

	public MongoDBSubmodelAPIFactory(BaSyxMongoDBConfiguration config) {
		this.config = config;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		MongoDBSubmodelAPI api = new MongoDBSubmodelAPI(config, submodel.getIdentification().getId());
		api.setSubmodel(submodel);
		return api;
	}

}
