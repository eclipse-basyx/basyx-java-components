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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPIFactory;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;

/**
 * Extends the {@link SubmodelAggregator} for the needs of MongoDB
 * 
 * @author schnicke, jungjan, witt
 *
 */
public class MongoDBSubmodelAggregator extends SubmodelAggregator {
	private MongoDBBaSyxStorageAPI<Submodel> storageApi;
	private IIdentifier shellId;

	public MongoDBSubmodelAggregator(ISubmodelAPIFactory submodelApiFactory, BaSyxMongoDBConfiguration config, MongoClient client) {
		this(submodelApiFactory, MongoDBBaSyxStorageAPIFactory.<Submodel>create(config.getSubmodelCollection(), Submodel.class, config, client));
	}

	public MongoDBSubmodelAggregator(ISubmodelAPIFactory submodelApiFactory, BaSyxMongoDBConfiguration config, MongoClient client, IIdentifier shellId) {
		this(submodelApiFactory, MongoDBBaSyxStorageAPIFactory.<Submodel>create(config.getSubmodelCollection(), Submodel.class, config, client));
		this.shellId = shellId;
	}

	public MongoDBSubmodelAggregator(ISubmodelAPIFactory submodelApiFactory, MongoDBBaSyxStorageAPI<Submodel> storageApi) {
		super(submodelApiFactory);
		this.storageApi = storageApi;
	}

	@Deprecated
	public MongoDBSubmodelAggregator(ISubmodelAPIFactory submodelApiFactory, BaSyxMongoDBConfiguration config) {
		this(submodelApiFactory, MongoDBBaSyxStorageAPIFactory.<Submodel>create(config.getSubmodelCollection(), Submodel.class, config));
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier submodelIdentifier) {
		storageApi.delete(submodelIdentifier.getId());
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		ISubmodel submodel = getSubmodelbyIdShort(idShort);
		storageApi.delete(submodel.getIdentification().getId());
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		MongoOperations mongoOperation = (MongoOperations) storageApi.getStorageConnection();
		Query hasParentId = query(where("parent.keys.[0].value").is(shellId.getId()));
		List<ISubmodel> submodels = mongoOperation.find(hasParentId, ISubmodel.class, storageApi.getCollectionName());
		return submodels;
	}



	@Override
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException {
		return storageApi.retrieve(identifier.getId());
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		storageApi.createOrUpdate(submodel);
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		storageApi.createOrUpdate(submodel);
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		storageApi.createOrUpdate((Submodel) submodelAPI.getSubmodel());
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException {
		Optional<ISubmodel> submodelOptional = getSubmodelList().stream().filter(submodel -> {
			return isTargetSubmodel(shellId, submodel, idShort);
		}).findAny();
		if (submodelOptional.isEmpty())
			throw new ResourceNotFoundException("The submodel with idShort '" + idShort + "' could not be found");
		return submodelOptional.get();
	}

	private boolean isTargetSubmodel(IIdentifier shellId, ISubmodel submodel, String idShort) {
		String shellIdValue = submodel.getParent().getKeys().get(0).getValue();
		return (submodel.getIdShort().equals(idShort)) && shellIdValue.equals(shellId.getId());
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException {
		Submodel submodel = (Submodel) getSubmodel(identifier);
		return new VABSubmodelAPIFactory().create(submodel);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException {
		Submodel submodel = (Submodel) getSubmodelbyIdShort(idShort);
		return new VABSubmodelAPIFactory().create(submodel);
	}
}
