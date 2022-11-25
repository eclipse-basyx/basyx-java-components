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
package org.eclipse.basyx.components.registry.configuration;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.components.configuration.BaSyxConfiguration;

/**
 * Represents a BaSyx registry configuration for a BaSyx Registry with any
 * backend, that can be loaded from a properties file.
 * 
 * @author espen, wege
 *
 */
public class BaSyxRegistryConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxRegistry_";

	// Feature enabling options
	private static final String FEATURE_ENABLED = "Enabled";
	private static final String FEATURE_DISABLED = "Disabled";

	// Default BaSyx Context configuration
	public static final String DEFAULT_BACKEND = RegistryBackend.INMEMORY.toString();
	public static final String DEFAULT_EVENTS = RegistryEventBackend.NONE.toString();
	public static final String DEFAULT_TAGGED_DIRECTORY = FEATURE_DISABLED;

	// Configuration keys
	public static final String ID = "registry.id";
	public static final String BACKEND = "registry.backend";
	public static final String EVENTS = "registry.events";

	private static final String TAGGED_DIRECTORY = "registry.taggedDirectory";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "registry.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_REGISTRY";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(BACKEND, DEFAULT_BACKEND);
		defaultProps.put(EVENTS, DEFAULT_EVENTS);
		defaultProps.put(TAGGED_DIRECTORY, DEFAULT_TAGGED_DIRECTORY);
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
		String[] properties = {
				BACKEND,
				EVENTS,
				TAGGED_DIRECTORY
		};
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public String getRegistryId() {
		return getProperty(ID);
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

	public boolean isTaggedDirectoryEnabled() {
		return getProperty(TAGGED_DIRECTORY).equals(FEATURE_ENABLED);
	}

	public void enableTaggedDirectory() {
		setProperty(TAGGED_DIRECTORY, FEATURE_ENABLED);
	}

	public void disableTaggedDirectory() {
		setProperty(TAGGED_DIRECTORY, FEATURE_DISABLED);
	}
}
