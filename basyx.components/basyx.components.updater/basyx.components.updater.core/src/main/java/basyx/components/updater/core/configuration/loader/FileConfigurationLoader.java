/*******************************************************************************

* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.core.configuration.loader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basyx.components.updater.core.configuration.parser.JsonParser;

/**
 * A core generic which can load the json configurations from a file
 * given in filepath to the corresponding mapper class
 *  
 * @author haque
 *
 */
public class FileConfigurationLoader {
	private static final Logger logger = LoggerFactory.getLogger(FileConfigurationLoader.class);
	private String filePath;
	private ClassLoader loader;
	private Class<?> mapperClass;
	
	/**
	 * An instance of {@link FileConfigurationLoader} which will load
	 * the jsons from fiven file path, to a mapper class.
	 * 
	 * The json file will be retrieved from the resource folder of given class loader
	 * 
	 * @param filePath
	 * @param loader
	 * @param mapperClass
	 */
	public FileConfigurationLoader(String filePath, ClassLoader loader, Class<?> mapperClass) {
		this.filePath = filePath;
		this.loader = loader;
		this.mapperClass = mapperClass;
	}
	
	/**
	 * Loads the json configuration from the file as a list of mapper object
	 * @return
	 */
	public Object loadListConfiguration() {
		Reader reader = getJsonReader();
		JsonParser parser = new JsonParser(mapperClass);
		return parser.getListConfiguration(reader);
	}
	
	/**
	 * Loads the json configuration from the file as a mapper object
	 * @return
	 */
	public Object loadConfiguration() {
		Reader reader = getJsonReader();
		JsonParser parser = new JsonParser(mapperClass);
		return parser.getConfiguration(reader);
	}
	
	/**
	 * Retrieves the input stream after loading the file from given 
	 * file path and the resource loader
	 * @return
	 */
	private InputStreamReader getJsonReader() {
		InputStream stream = loader.getResourceAsStream(filePath);
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(stream, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("Could not find the file");
			e.printStackTrace();
		}
		return reader;
	}
}
