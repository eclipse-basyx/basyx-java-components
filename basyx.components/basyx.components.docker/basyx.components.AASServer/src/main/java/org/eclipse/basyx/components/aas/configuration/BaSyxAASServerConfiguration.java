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
package org.eclipse.basyx.components.aas.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.basyx.components.configuration.BaSyxConfiguration;

import com.google.gson.Gson;

/**
 * Represents a BaSyx server configuration for an AAS Server with any backend,
 * that can be loaded from a properties file.
 * 
 * @author espen
 *
 */
public class BaSyxAASServerConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxAAS_";

	// Feature enabling options
	private static final String FEATURE_ENABLED = "Enabled";
	private static final String FEATURE_DISABLED = "Disabled";

	// Default BaSyx AAS configuration
	public static final String DEFAULT_BACKEND = AASServerBackend.INMEMORY.toString();
	public static final String DEFAULT_HOSTPATH = "";
	public static final String DEFAULT_SUBMODELS = "[]";
	public static final String DEFAULT_SOURCE = "";
	public static final String DEFAULT_REGISTRY = "";
	public static final String DEFAULT_EVENTS = AASEventBackend.NONE.toString();
	public static final String DEFAULT_AASX_UPLOAD = FEATURE_ENABLED;
	public static final String DEFAULT_AUTHORIZATION = FEATURE_DISABLED;

	// Configuration keys
	public static final String REGISTRY = "registry.path";
	public static final String HOSTPATH = "registry.host";
	public static final String SUBMODELS = "registry.submodels";
	public static final String BACKEND = "aas.backend";
	public static final String SOURCE = "aas.source";
	public static final String EVENTS = "aas.events";
	public static final String AASX_UPLOAD = "aas.aasxUpload";
	public static final String AUTHORIZATION = "aas.authorization";

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "aas.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_AAS";

	public static final String PATTERN = "^\\[\\\".*\\\"\\]$";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(BACKEND, DEFAULT_BACKEND);
		defaultProps.put(SOURCE, DEFAULT_SOURCE);
		defaultProps.put(REGISTRY, DEFAULT_REGISTRY);
		defaultProps.put(HOSTPATH, DEFAULT_HOSTPATH);
		defaultProps.put(SUBMODELS, DEFAULT_SUBMODELS);
		defaultProps.put(EVENTS, DEFAULT_EVENTS);
		defaultProps.put(AASX_UPLOAD, DEFAULT_AASX_UPLOAD);
		defaultProps.put(AUTHORIZATION, DEFAULT_AUTHORIZATION);
		return defaultProps;
	}

	/**
	 * Empty Constructor - use default values
	 */
	public BaSyxAASServerConfiguration() {
		super(getDefaultProperties());
	}

	/**
	 * Constructor with initial configuration
	 * 
	 * @param backend
	 *            The backend for the AASServer
	 * @param source
	 *            The file source for the AASServer (e.g. an .aasx file)
	 */
	public BaSyxAASServerConfiguration(AASServerBackend backend, String source) {
		super(getDefaultProperties());
		setAASBackend(backend);
		setAASSourceAsList(source);
	}

	/**
	 * Constructor with initial configuration values
	 * 
	 * @param backend
	 *            The backend for the AASServer
	 * @param source
	 *            The file source for the AASServer (e.g. an .aasx file)
	 * @param registryUrl
	 *            The url to the registry
	 */
	public BaSyxAASServerConfiguration(AASServerBackend backend, String source, String registryUrl) {
		this(backend, source);
		setRegistry(registryUrl);
	}

	/**
	 * Constructor with initial configuration values
	 * 
	 * @param backend
	 *            The backend for the AASServer
	 * @param source
	 *            The file source for the AASServer (e.g. an .aasx file)
	 * @param registryUrl
	 *            The url to the registry
	 * @param hostPath
	 *            The root path to the aas component, when it is deployed. Address
	 *            could be used for registration at a registry.
	 */
	public BaSyxAASServerConfiguration(AASServerBackend backend, String source, String registryUrl, String hostPath) {
		this(backend, source, registryUrl);
		setHostpath(hostPath);
	}

	/**
	 * Constructor with predefined value map
	 */
	public BaSyxAASServerConfiguration(Map<String, String> values) {
		super(values);
	}

	public void loadFromEnvironmentVariables() {
		String[] properties = { REGISTRY, BACKEND, SOURCE, EVENTS, HOSTPATH, AASX_UPLOAD, AUTHORIZATION };
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public AASServerBackend getAASBackend() {
		return AASServerBackend.fromString(getProperty(BACKEND));
	}

	public void setAASBackend(AASServerBackend backend) {
		setProperty(BACKEND, backend.toString());
	}

	public AASEventBackend getAASEvents() {
		return AASEventBackend.fromString(getProperty(EVENTS));
	}

	public void setAASEvents(AASEventBackend events) {
		setProperty(EVENTS, events.toString());
	}

	public boolean isAASXUploadEnabled() {
		return getProperty(AASX_UPLOAD).equals(FEATURE_ENABLED);
	}

	public void enableAASXUpload() {
		setProperty(AASX_UPLOAD, FEATURE_ENABLED);
	}

	public void disableAASXUpload() {
		setProperty(AASX_UPLOAD, FEATURE_DISABLED);
	}

	/**
	 * @deprecated This method is deprecated due to a new implementation to support
	 *             multiple serialized AAS. Use new method
	 *             {@link #getAASSourceAsList() } to get the list of source
	 *             path/paths of serialized AAS.
	 */
	@Deprecated
	public String getAASSource() {
		return getProperty(SOURCE);
	}

	public List<String> getAASSourceAsList() {
		if (!areMultipleAasSourcesProvided(getProperty(SOURCE))) {
			return Arrays.asList(getProperty(SOURCE));
		}

		return parseFromJson(getProperty(SOURCE));
	}

	private boolean areMultipleAasSourcesProvided(String property) {
		return Pattern.matches(PATTERN, property);
	}

	/**
	 * @deprecated This method is deprecated due to a new implementation to support
	 *             multiple serialized AAS. Use new method
	 *             {@link #setAASSourceAsList(String) } to get the list of source
	 *             path/paths of serialized AAS.
	 */
	@Deprecated
	public void setAASSource(String source) {
		setProperty(SOURCE, source);
	}

	public void setAASSourceAsList(String source) {
		setProperty(SOURCE, source);
	}

	public String getRegistry() {
		return getProperty(REGISTRY);
	}

	public void setRegistry(String registryPath) {
		setProperty(REGISTRY, registryPath);
	}

	public List<String> getSubmodels() {
		return parseFromJson(getProperty(SUBMODELS));
	}

	public void setSubmodels(List<String> submodels) {
		setProperty(SUBMODELS, serializeSubmodels(submodels));
	}

	public String getHostpath() {
		return getProperty(HOSTPATH);
	}

	public void setHostpath(String hostPath) {
		setProperty(HOSTPATH, hostPath);
	}

	@SuppressWarnings("unchecked")
	private List<String> parseFromJson(String property) {
		List<String> fromJson = new Gson().fromJson(property, List.class);
		if (fromJson == null) {
			return new ArrayList<>();
		} else {
			return fromJson;
		}
	}

	private String serializeSubmodels(List<String> submodels) {
		return new Gson().toJson(submodels);
	}

	public boolean isAuthorizationEnabled() {
		return getProperty(AUTHORIZATION).equals(FEATURE_ENABLED);
	}

	public void enableAuthorization() {
		setProperty(AUTHORIZATION, FEATURE_ENABLED);
	}

	public void disableAuthorization() {
		setProperty(AUTHORIZATION, FEATURE_DISABLED);
	}
}
