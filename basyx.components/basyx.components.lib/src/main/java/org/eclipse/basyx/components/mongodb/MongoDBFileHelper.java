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

package org.eclipse.basyx.components.mongodb;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Filters;

public class MongoDBFileHelper {
	private MongoDBFileHelper() {
	}

	@SuppressWarnings("unchecked")
	public static String updateFileInDB(MongoClient client, BaSyxMongoDBConfiguration config, String submodelId, InputStream newValue, ISubmodelElement element, String idShortPath) {
		File file = File.createAsFacade((Map<String, Object>) element);
		GridFSBucket bucket = getGridFSBucket(client, config);
		String fileName = MongoDBHelper.constructFileName(submodelId, file, idShortPath);
		deleteAllDuplicateFiles(bucket, fileName);
		bucket.uploadFromStream(fileName, newValue);
		return fileName;
	}

	@SuppressWarnings("unchecked")
	public static void deleteAllFilesFromGridFsIfIsFileSubmodelElement(MongoClient client, BaSyxMongoDBConfiguration config, Submodel sm, String idShort) {
		Map<String, Object> submodelElement = (Map<String, Object>) sm.getSubmodelElement(idShort);
		if (!File.isFile(submodelElement))
			return;
		File file = File.createAsFacade(submodelElement);
		GridFSBucket bucket = MongoDBFileHelper.getGridFSBucket(client, config);
		bucket.find(Filters.eq("filename", file.getValue())).forEach(gridFile -> bucket.delete(gridFile.getObjectId()));
	}

	public static GridFSBucket getGridFSBucket(MongoClient client, BaSyxMongoDBConfiguration config) {
		MongoDatabase database = client.getDatabase(config.getDatabase());
		return GridFSBuckets.create(database, config.getFileCollection());
	}

	private static void deleteAllDuplicateFiles(GridFSBucket bucket, String fileName) {
		bucket.find(Filters.eq("filename", fileName)).forEach(gridFile -> bucket.delete(gridFile.getObjectId()));
	}

}
