/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.registry.configuration;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.components.configuration.BaSyxConfiguration;

/**
 * Represents a BaSyx registry configuration for a BaSyx Registry with any backend,
 * that can be loaded from a properties file.
 * 
 * @author espen
 *
 */
public class BaSyxRegistryConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxRegistry_";

	// Default BaSyx Context configuration
	public static final String DEFAULT_BACKEND = RegistryBackend.INMEMORY.toString();
	public static final String DEFAULT_EVENTS = RegistryEventBackend.NONE.toString();
	public static final String DEFAULT_AUTHORIZATION_ENABLED = "false";

	// Configuration keys
	public static final String BACKEND = "registry.backend";
	public static final String EVENTS = "registry.events";
	public static final String AUTHORIZATION_ENABLED = "registry.authorizationEnabled";
	private static final String TAGGED_DIRECTORY_ENABLED = "registry.taggedDirectoryEnabled";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "registry.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_REGISTRY";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(BACKEND, DEFAULT_BACKEND);
		defaultProps.put(EVENTS, DEFAULT_EVENTS);
		defaultProps.put(AUTHORIZATION_ENABLED, DEFAULT_AUTHORIZATION_ENABLED);
		return defaultProps;
	}

	public BaSyxRegistryConfiguration() {
		super(getDefaultProperties());
	}

	public BaSyxRegistryConfiguration(RegistryBackend backend) {
		super(getDefaultProperties());
		setRegistryBackend(backend);
	}

	public BaSyxRegistryConfiguration(Map<String, String> values) {
		super(values);
	}

	public void loadFromEnvironmentVariables() {
		loadFromEnvironmentVariables(ENV_PREFIX, BACKEND, EVENTS, AUTHORIZATION_ENABLED);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public RegistryBackend getRegistryBackend() {
		return RegistryBackend.fromString(getProperty(BACKEND));
	}

	public void setRegistryBackend(RegistryBackend backend) {
		setProperty(BACKEND, backend.toString());
	}

	public RegistryEventBackend getRegistryEvents() {
		return RegistryEventBackend.fromString(getProperty(EVENTS));
	}

	public void setRegistryEvents(RegistryEventBackend events) {
		setProperty(EVENTS, events.toString());
	}

	public boolean isAuthorizationEnabled() {
		return Boolean.parseBoolean(getProperty(AUTHORIZATION_ENABLED));
	}

	public void setAuthorizationEnabled(boolean authorizationEnabled) {
		setProperty(AUTHORIZATION_ENABLED, Boolean.toString(authorizationEnabled));
	}

	public boolean isTaggedDirectoryEnabled() {
		return Boolean.parseBoolean(getProperty(TAGGED_DIRECTORY_ENABLED));
	}

	public void setTaggedDirectoryEnabled(boolean taggedDirectoryEnabled) {
		setProperty(TAGGED_DIRECTORY_ENABLED, Boolean.toString(taggedDirectoryEnabled));
	}
}
