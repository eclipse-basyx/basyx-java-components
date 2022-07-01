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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * Extends the {@link SubmodelAggregator} for the needs of MongoDB
 * 
 * @author schnicke
 *
 */
public class MongoDBSubmodelAggregator extends SubmodelAggregator {

	private String smCollection;
	private MongoTemplate mongoOps;

	@Deprecated
	public MongoDBSubmodelAggregator(ISubmodelAPIFactory smApiFactory, BaSyxMongoDBConfiguration config) {
		this(smApiFactory, config, MongoClients.create(config.getConnectionUrl()));
	}

	public MongoDBSubmodelAggregator(ISubmodelAPIFactory smApiFactory, BaSyxMongoDBConfiguration config, MongoClient client) {
		super(smApiFactory);

		smCollection = config.getSubmodelCollection();

		mongoOps = new MongoTemplate(client, config.getDatabase());
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		super.deleteSubmodelByIdentifier(identifier);
		deleteSubmodelFromDB(identifier);
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		try {
			ISubmodel sm = getSubmodelbyIdShort(idShort);
			super.deleteSubmodelByIdShort(idShort);
			deleteSubmodelFromDB(sm.getIdentification());
		} catch (ResourceNotFoundException e) {
			// Nothing to do
		}
	}

	private void deleteSubmodelFromDB(IIdentifier identifier) {
		Query hasId = query(where(MongoDBSubmodelAPI.SMIDPATH).is(identifier.getId()));
		mongoOps.remove(hasId, smCollection);
	}
}
