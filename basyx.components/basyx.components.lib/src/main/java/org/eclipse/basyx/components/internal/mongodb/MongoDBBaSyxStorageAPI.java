/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.components.internal.mongodb;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.extensions.internal.storage.BaSyxStorageAPI;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.Identifiable;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.result.DeleteResult;

/**
 * Provides BaSyxStorageAPI implementation for MongoDB
 * 
 * @author fischer, jung
 *
 * @param <T>
 */
public class MongoDBBaSyxStorageAPI<T> extends BaSyxStorageAPI<T> {
	private static final String INDEX_KEY = Identifiable.IDENTIFICATION + "." + Identifier.ID;

	protected BaSyxMongoDBConfiguration config;
	protected MongoClient client;
	protected MongoOperations mongoOps;

	public MongoDBBaSyxStorageAPI(String collectionName, Class<T> type, BaSyxMongoDBConfiguration config) {
		this(collectionName, type, config, MongoClients.create(config.getConnectionUrl()));
	}

	public MongoDBBaSyxStorageAPI(String collectionName, Class<T> type, BaSyxMongoDBConfiguration config, MongoClient client) {
		super(collectionName, type);
		this.config = config;
		this.client = client;
		this.mongoOps = new MongoTemplate(client, config.getDatabase());
		this.configureIndexKey();
	}

	private void configureIndexKey() {
		TextIndexDefinition idIndex = TextIndexDefinition.builder().onField(INDEX_KEY).build();
		this.mongoOps.indexOps(TYPE).ensureIndex(idIndex);
	}

	@Override
	public T createOrUpdate(T obj) {
		String key = getKey(obj);
		if (alreadyExists(obj, key)) {
			return update(obj, key);
		}

		T created = mongoOps.insert(obj, COLLECTION_NAME);
		created = removeMongoDBSpecificMapAttribute(created);

		return created;
	}

	private boolean alreadyExists(T obj, String key) {
		Query hasId = query(where(INDEX_KEY).is(key));
		boolean exists = mongoOps.exists(hasId, this.TYPE);
		return exists;
	}

	@Override
	public T update(T obj, String key) {
		Query hasId = query(where(INDEX_KEY).is(key));
		T replaced = mongoOps.findAndReplace(hasId, obj, COLLECTION_NAME);
		if (replaced == null) {
			return createOrUpdate(obj);
		}
		return replaced;
	}

	@SuppressWarnings("unchecked")
	private T removeMongoDBSpecificMapAttribute(T created) {
		if (created instanceof Map)
			((Map<String, Object>) created).remove("_id");
		return created;
	}

	@Override
	public boolean delete(String key) {
		Query hasId = query(where(INDEX_KEY).is(key));
		DeleteResult result = mongoOps.remove(hasId, COLLECTION_NAME);
		return result.getDeletedCount() == 1L;
	}

	@Override
	public void createCollectionIfNotExists(String collectionName) {
		// With MongoOperations Collections are created implicitly
	}

	@Override
	public void deleteCollection() {
		mongoOps.dropCollection(COLLECTION_NAME);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T rawRetrieve(String key) {
		Query hasId = query(where(INDEX_KEY).is(key));
		var result = mongoOps.findOne(hasId, TYPE, COLLECTION_NAME);
		if (result == null) {
			throw new ResourceNotFoundException("The submodel " + key + " could not be found in the database.");
		}
		if (isBaSyxType(TYPE)) {
			((Map<String, Object>) result).remove("_id");
		}
		return (T) result;
	}

	@Override
	public java.io.File getFile(String idShortPath, String parentKey, Map<String, Object> objMap) {
		try {
			File fileSubmodelElement = File.createAsFacade(objMap);
			GridFSBucket bucket = MongoDBFileHelper.getGridFSBucket(client, config);
			String fileName = MongoDBFileHelper.constructFileName(parentKey, fileSubmodelElement, idShortPath);
			java.io.File file = new java.io.File(fileName);
			FileOutputStream fileOutputStream;
			fileOutputStream = new FileOutputStream(file);
			bucket.downloadToStream(fileName, fileOutputStream);
			return file;
		} catch (FileNotFoundException e) {
			throw new ResourceNotFoundException("The File Submodel Element does not contain a File");
		}
	}

	@Override
	public String writeFile(String idShortPath, String parentKey, InputStream inputStream, ISubmodelElement element) {
		return MongoDBFileHelper.updateFileInDB(client, config, parentKey, inputStream, element, idShortPath);
	}

	@Override
	public void deleteFile(Submodel submodel, String idShort) {
		MongoDBFileHelper.deleteAllFilesFromGridFsIfIsFileSubmodelElement(client, config, submodel, idShort);
	}

	@Override
	public Collection<T> rawRetrieveAll() {
		Collection<T> data = mongoOps.findAll(TYPE, COLLECTION_NAME);
		return data;
	}
}
