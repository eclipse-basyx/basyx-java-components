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

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;

/**
 * 
 * Factory for creating a MongoDBAASAPI
 * 
 * @author fried
 *
 */
public class MongoDBAASAPIFactory implements IAASAPIFactory {

	private BaSyxMongoDBConfiguration config;

	public MongoDBAASAPIFactory(BaSyxMongoDBConfiguration config) {
		this.config = config;
	}

	@Override
	public IAASAPI getAASApi(AssetAdministrationShell aas) {
		MongoDBAASAPI api = new MongoDBAASAPI(config, aas.getIdentification().getId());
		api.setAAS(aas);
		return api;
	}

}
