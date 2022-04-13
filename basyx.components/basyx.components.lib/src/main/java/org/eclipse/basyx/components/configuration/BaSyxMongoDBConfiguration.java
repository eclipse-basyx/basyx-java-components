/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.components.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a BaSyx configuration for a MongoDB connection.
 * 
 * @author espen
 *
 */
public class BaSyxMongoDBConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxMongoDB_";

	// Default BaSyx SQL configuration
	public static final String DEFAULT_CONNECTIONURL = "mongodb://127.0.0.1:27017/";
	public static final String DEFAULT_DATABASE = "admin";
	public static final String DEFAULT_REGISTRY_COLLECTION = "basyxregistry";
	public static final String DEFAULT_AAS_COLLECTION = "basyxaas";
	public static final String DEFAULT_SUBMODEL_COLLECTION = "basyxsubmodel";

	public static final String DATABASE = "dbname";
	public static final String CONNECTIONURL = "dbconnectionstring";
	public static final String REGISTRY_COLLECTION = "dbcollectionRegistry";
	public static final String AAS_COLLECTION = "dbcollectionAAS";
	public static final String SUBMODEL_COLLECTION = "dbcollectionSubmodels";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "mongodb.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_MONGODB";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(CONNECTIONURL, DEFAULT_CONNECTIONURL);
		defaultProps.put(DATABASE, DEFAULT_DATABASE);
		defaultProps.put(REGISTRY_COLLECTION, DEFAULT_REGISTRY_COLLECTION);
		defaultProps.put(AAS_COLLECTION, DEFAULT_AAS_COLLECTION);
		defaultProps.put(SUBMODEL_COLLECTION, DEFAULT_SUBMODEL_COLLECTION);

		return defaultProps;
	}

	/**
	 * Constructor with predefined value map
	 */
	public BaSyxMongoDBConfiguration(Map<String, String> values) {
		super(values);
	}

	/**
	 * Empty Constructor - use default values
	 */
	public BaSyxMongoDBConfiguration() {
		super(getDefaultProperties());
	}

	/**
	 * Constructor with initial configuration
	 * 
	 * @param connectionUrl
	 *            Connection-URL for the mongodb
	 * @param database
	 *            The database that shall be used
	 * @param registryCollection
	 *            Collection name for the registry data
	 * @param aasCollection
	 *            Collection name for the AAS data
	 * @param submodelCollection
	 *            Collection name for the submodel data
	 */
	public BaSyxMongoDBConfiguration(String connectionUrl, String database, String registryCollection, String aasCollection, String submodelCollection) {
		this();
		setConnectionUrl(connectionUrl);
		setDatabase(database);
		setRegistryCollection(registryCollection);
		setAASCollection(aasCollection);
		setSubmodelCollection(submodelCollection);
	}

	/**
	 * Constructor with initial configuration (without registry collection)
	 * 
	 * @param connectionUrl
	 *            Connection-URL for the mongodb
	 * @param database
	 *            The database that shall be used
	 * @param aasCollection
	 *            Collection name for the AAS data
	 * @param submodelCollection
	 *            Collection name for the submodel data
	 */
	public BaSyxMongoDBConfiguration(String connectionUrl, String database, String aasCollection, String submodelCollection) {
		this();
		setConnectionUrl(connectionUrl);
		setDatabase(database);
		setAASCollection(aasCollection);
		setSubmodelCollection(submodelCollection);
	}

	/**
	 * Constructor with initial configuration (without aas collection)
	 * 
	 * @param connectionUrl
	 *            Connection-URL for the mongodb
	 * @param database
	 *            The database that shall be used
	 * @param registryCollection
	 *            collection of the registry
	 */
	public BaSyxMongoDBConfiguration(String connectionUrl, String database, String registryCollection) {
		this();
		setConnectionUrl(connectionUrl);
		setDatabase(database);
		setRegistryCollection(registryCollection);
	}

	public void loadFromEnvironmentVariables() {
		String[] properties = { DATABASE, CONNECTIONURL, REGISTRY_COLLECTION, AAS_COLLECTION, SUBMODEL_COLLECTION };
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public String getDatabase() {
		return getProperty(DATABASE);
	}

	public void setDatabase(String database) {
		setProperty(DATABASE, database);
	}

	public String getConnectionUrl() {
		return getProperty(CONNECTIONURL);
	}

	public void setConnectionUrl(String connectionUrl) {
		setProperty(CONNECTIONURL, connectionUrl);
	}

	public String getRegistryCollection() {
		return getProperty(REGISTRY_COLLECTION);
	}

	public void setRegistryCollection(String registryCollection) {
		setProperty(REGISTRY_COLLECTION, registryCollection);
	}

	public String getAASCollection() {
		return getProperty(AAS_COLLECTION);
	}

	public void setAASCollection(String aasCollection) {
		setProperty(AAS_COLLECTION, aasCollection);
	}

	public String getSubmodelCollection() {
		return getProperty(SUBMODEL_COLLECTION);
	}

	public void setSubmodelCollection(String submodelCollection) {
		setProperty(SUBMODEL_COLLECTION, submodelCollection);
	}
}
