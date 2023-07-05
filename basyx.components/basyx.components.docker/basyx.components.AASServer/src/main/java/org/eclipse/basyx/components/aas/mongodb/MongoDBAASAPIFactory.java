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
package org.eclipse.basyx.components.aas.mongodb;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPIFactory;

import com.mongodb.client.MongoClient;

/**
 * 
 * Factory for creating a MongoDBAASAPI
 * 
 * @author fried, jung
 *
 */
public class MongoDBAASAPIFactory implements IAASAPIFactory {

	private MongoDBBaSyxStorageAPI<AssetAdministrationShell> storageAPI;

	@Deprecated
	public MongoDBAASAPIFactory(BaSyxMongoDBConfiguration config) {
		this(MongoDBBaSyxStorageAPIFactory.<AssetAdministrationShell>create(config.getAASCollection(), AssetAdministrationShell.class, config));
	}

	public MongoDBAASAPIFactory(BaSyxMongoDBConfiguration config, MongoClient client) {
		this(MongoDBBaSyxStorageAPIFactory.<AssetAdministrationShell>create(config.getAASCollection(), AssetAdministrationShell.class, config, client));
	}

	public MongoDBAASAPIFactory(MongoDBBaSyxStorageAPI<AssetAdministrationShell> mongoDBStorageAPI) {
		this.storageAPI = mongoDBStorageAPI;
	}

	@Override
	public IAASAPI getAASApi(AssetAdministrationShell sehll) {
		new MongoDBAASAPI(storageAPI, sehll.getIdentification().getId());
		MongoDBAASAPI api = new MongoDBAASAPI(storageAPI, sehll.getIdentification().getId());
		api.setAAS(sehll);
		return api;
	}

}
