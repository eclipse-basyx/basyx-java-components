/*******************************************************************************
 * Copyright (C) 2022, 2023 the Eclipse BaSyx Authors
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

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

import com.mongodb.client.MongoClient;

/**
 * Extends the {@link SubmodelAggregator} for the needs of MongoDB
 * 
 * @author schnicke, jungjan
 *
 */
public class MongoDBSubmodelAggregator extends SubmodelAggregator {
	private MongoDBBaSyxStorageAPI<Submodel> storageApi;

	@Deprecated
	public MongoDBSubmodelAggregator(ISubmodelAPIFactory smApiFactory, BaSyxMongoDBConfiguration config) {
		this(smApiFactory, new MongoDBBaSyxStorageAPI<>(config.getSubmodelCollection(), Submodel.class, config));
	}

	public MongoDBSubmodelAggregator(ISubmodelAPIFactory smApiFactory, BaSyxMongoDBConfiguration config, MongoClient client) {
		this(smApiFactory, new MongoDBBaSyxStorageAPI<>(config.getSubmodelCollection(), Submodel.class, config, client));
	}

	public MongoDBSubmodelAggregator(ISubmodelAPIFactory submodelApiFactory, MongoDBBaSyxStorageAPI<Submodel> storageApi) {
		super(submodelApiFactory);
		this.storageApi = storageApi;
	}


	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		super.deleteSubmodelByIdentifier(identifier);
		storageApi.delete(identifier.getId());
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		ISubmodel submodel = getSubmodelbyIdShort(idShort);
		super.deleteSubmodelByIdShort(idShort);
		storageApi.delete(submodel.getIdentification().getId());
	}

}
