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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.File;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Filters;

/**
 * Supports MongoDB file handling
 * 
 * @author fischer
 *
 */
public class MongoDBFileHelper {
	private MongoDBFileHelper() {
	}

	@SuppressWarnings("unchecked")
	public static String updateFileInDB(MongoClient client, BaSyxMongoDBConfiguration config, String submodelId, InputStream newValue, ISubmodelElement element, String idShortPath) {
		File file = File.createAsFacade((Map<String, Object>) element);
		GridFSBucket bucket = getGridFSBucket(client, config);
		String fileName = constructFileName(submodelId, file, idShortPath);
		deleteAllDuplicateFiles(bucket, fileName, legacyFileName(submodelId, file, idShortPath));
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
		deleteAllDuplicateFiles(bucket, constructFileName(sm.getIdentification().getId(), file, idShort), legacyFileName(sm.getIdentification().getId(), file, idShort));
	}

	protected static boolean fileExists(GridFSBucket bucket, String fileName) {
		return bucket.find(Filters.eq("filename", fileName)).first() != null;
	}

	public static GridFSBucket getGridFSBucket(MongoClient client, BaSyxMongoDBConfiguration config) {
		MongoDatabase database = client.getDatabase(config.getDatabase());
		return GridFSBuckets.create(database, config.getFileCollection());
	}

	private static void deleteAllDuplicateFiles(GridFSBucket bucket, String... fileNames) {
		bucket.find(Filters.or(
						Arrays.stream(fileNames)
								.map(fileName -> Filters.eq("filename", fileName))
								.collect(Collectors.toList())))
				.forEach(gridFile -> bucket.delete(gridFile.getObjectId()));
	}

	public static String constructFileName(String submodelId, File file, String idShortPath) {
		String fileName = submodelId + "-" + idShortPath.replaceAll("/", "-") + getFileExtension(file);
		// replace those chars that are not permitted on filesystems
		fileName = fileName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
		// ensure the filename is still unique to be used as key in MongoDB storage layer
		return String.format("#%s#", Objects.hashCode(submodelId)).concat(fileName);
	}
	/**
	 * This method is kept for backwards compatibility to still find files from DB which where stored with old scheme.
	 * @param file the filename of this file is requested.
	 * @param idShortPath the shortId of the given file.
	 * @return the filename as it was constructed in older versions.
	 */
	public static String legacyFileName(String submodelId, File file, String idShortPath) {
		return submodelId + "-" + idShortPath.replaceAll("/", "-") + getFileExtension(file);
	}


	private static String getFileExtension(File file) {
		MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
		try {
			MimeType mimeType = allTypes.forName(file.getMimeType());
			return mimeType.getExtension();
		} catch (MimeTypeException e) {
			e.printStackTrace();
			return "";
		}
	}

}
