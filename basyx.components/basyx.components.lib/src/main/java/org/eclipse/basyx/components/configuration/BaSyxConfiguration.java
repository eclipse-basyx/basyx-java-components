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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakRoleAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaSyxConfiguration {
	private static Logger logger = LoggerFactory.getLogger(BaSyxConfiguration.class);

	// Properties in this configuration
	private Map<String, String> values;

	/**
	 * Constructor that takes the configuration's default values. All the keys in
	 * the map are the name of the properties that are stored and loaded in this
	 * configuration.
	 */
	public BaSyxConfiguration(Map<String, String> defaultValues) {
		this.values = defaultValues;
	}

	public static InputStream getResourceStream(String relativeResourcePath) {
		ClassLoader classLoader = BaSyxConfiguration.class.getClassLoader();
		return classLoader.getResourceAsStream(relativeResourcePath);
	}

	public static String getResourceString(String relativeResourcePath) throws IOException {
		ClassLoader classLoader = BaSyxConfiguration.class.getClassLoader();
		return IOUtils.resourceToString(relativeResourcePath, StandardCharsets.UTF_8, classLoader);
	}

	/**
	 * Load the configuration from a path relative to the current folder
	 * 
	 * @param filePath
	 *            Path to the resource in the application folder
	 */
	public void loadFromFile(String filePath) {
		try (InputStream fileStream = new FileInputStream(filePath)) {
			logger.info("Loading properties from file '" + filePath + "'");
			loadFromStream(fileStream);
		} catch (FileNotFoundException e) {
			logger.error("Configuration file not found: '" + filePath + "'", e);
		} catch (IOException e) {
			logger.error("Configuration io error: '" + filePath + "'", e);
		}
	}

	/**
	 * Load the configuration from a path relative to the current folder
	 * 
	 * @param fileKey
	 *            key where the file path to the resource in the application folder
	 */
	public void loadFileOrDefaultResource(String fileKey, String defaultResource) {
		// Try to load property that points to the configuration file (e.g. java
		// -DfileKey=yx.properties [...])
		String configFilePath = System.getProperty(fileKey);
		if (configFilePath == null || configFilePath.isEmpty()) {
			// Try to load environment variable that points to the configuration file
			configFilePath = System.getenv(fileKey);
		}

		// Load context configuration
		if (configFilePath != null && !configFilePath.isEmpty()) {
			// file path available? => load configs from file
			loadFromFile(configFilePath);
		} else {
			// fallback: load default configs (by resource)
			loadFromResource(defaultResource);
		}
	}

	/**
	 * Load the configuration from an input stream
	 * 
	 * @param input
	 *            the input stream containing the properties
	 */
	public void loadFromStream(InputStream input) {
		logger.info("Loading from inputStream: " + input);
		Properties properties = new Properties();
		try {
			properties.load(input);
			loadFromProperties(properties);
		} catch (IOException e) {
			logger.error("Could not load properties", e);
		}
	}

	/**
	 * Load the configuration from a path relative to the current resource folder
	 * 
	 * @param relativeResourcePath
	 *            Path to the resource in the resource folder. In a maven project,
	 *            the resources are located at /src/main/resources by default.
	 */
	public void loadFromResource(String relativeResourcePath) {
		logger.info("Loading properties from resource '" + relativeResourcePath + "'");
		try (InputStream resourceStream = getResourceStream(relativeResourcePath)) {
			if (resourceStream == null) {
				logger.info("Could not get resource stream!");
			}
			loadFromStream(resourceStream);
		} catch (FileNotFoundException e) {
			logger.error("Configuration resource not found: '" + relativeResourcePath + "'", e);
		} catch (IOException e) {
			logger.error("Configuration io error: '" + relativeResourcePath + "'", e);
		}
	}

	/**
	 * Load the configuration directly from properties.
	 */
	public void loadFromProperties(Properties properties) {
		for (Object property : properties.keySet()) {
			String propertyName = (String) property;
			String loaded = properties.getProperty(propertyName);
			if (values.containsKey(propertyName)) {
				logger.info(propertyName + ": '" + loaded + "'");
			} else {
				logger.debug(propertyName + ": '" + loaded + "'");
			}
			values.put(propertyName, loaded);
		}
	}

	/**
	 * Method for subclasses to read specific environment variables
	 * 
	 * @param prefix
	 *            The prefix of each of the environment variables
	 * @param properties
	 *            The name of the properties in the config and environment (with
	 *            prefix)
	 */
	protected void loadFromEnvironmentVariables(String prefix, String... properties) {
		try {
			Map<String, String> allEnvironmentVariables = System.getenv();

			boolean usesLowerCaseNamingConvention = usesLowerCaseNamingConvention(allEnvironmentVariables, prefix);
			boolean usesDeprecatedNamingConvention = usesDeprecatedNamingConvention(allEnvironmentVariables, prefix);

			if (usesDeprecatedNamingConvention && usesLowerCaseNamingConvention) {
				throw createEnvironmentVariableWrongUsageException();
			}

			for (String propName : properties) {
				String result = getEnvironmentVariable(prefix, propName, usesDeprecatedNamingConvention);
				if (result != null) {
					logger.info("Environment - " + propName + ": " + result);
					setProperty(propName, result);
				}
			}
		} catch (SecurityException e) {
			logger.info("Reading configs from environment is not permitted: " + e);
		}
	}

	private String getEnvironmentVariable(String prefix, String propName, boolean usesDeprecatedNamingConvention) {
		if (usesDeprecatedNamingConvention) {
			return getEnvironmentVariableDeprecatedNamingConvention(prefix, propName);
		} else {
			return getEnvironmentVariableLowerCaseNamingConvention(prefix, propName);
		}
	}

	private String getEnvironmentVariableLowerCaseNamingConvention(String prefix, String propName) {
		return System.getenv(changeToUnderscoreAndLowerCase(prefix + propName));
	}

	private String getEnvironmentVariableDeprecatedNamingConvention(String prefix, String propName) {
		return System.getenv(prefix + propName);
	}

	private boolean usesLowerCaseNamingConvention(Map<String, String> allEnvironmentVariables, String prefix) {
		List<String> variableWithLowercasePrefix = getEnvironmentVariablesLowerCaseNamingConvention(allEnvironmentVariables, prefix);

		if (containsUpperCasePrefixVariables(variableWithLowercasePrefix)) {
			throw createEnvironmentVariableWrongUsageException();
		}

		return !variableWithLowercasePrefix.isEmpty();
	}

	private boolean containsUpperCasePrefixVariables(List<String> variableWithLowercasePrefix) {
		return variableWithLowercasePrefix.stream().anyMatch(varName -> nameContainsUppercase(varName));
	}

	private boolean usesDeprecatedNamingConvention(Map<String, String> allEnvironmentVariables, String prefix) {
		List<String> variableWithPrefix = getEnvironmentVariablesDeprecatedNamingConvention(allEnvironmentVariables, prefix);

		if (containsDotAndUnderscoreInDeprecatedNamingConvention(variableWithPrefix, prefix)) {
			throw createEnvironmentVariableWrongUsageException();
		}

		return !variableWithPrefix.isEmpty();
	}

	private boolean containsDotAndUnderscoreInDeprecatedNamingConvention(List<String> variableWithLowercasePrefix, String prefix) {
		return variableWithLowercasePrefix.stream().anyMatch(varName -> nameWihoutPrefixContainsUnderscore(varName, prefix));
	}

	private boolean nameWihoutPrefixContainsUnderscore(String varName, String prefix) {
		return varName.replace(prefix, "").contains("_");
	}

	private List<String> getEnvironmentVariablesLowerCaseNamingConvention(Map<String, String> allEnvironmentVariables, String prefix) {
		return allEnvironmentVariables.keySet().stream().filter(x -> x.startsWith(changeToUnderscoreAndLowerCase(prefix))).collect(Collectors.toList());
	}

	private List<String> getEnvironmentVariablesDeprecatedNamingConvention(Map<String, String> allEnvironmentVariables, String prefix) {
		return allEnvironmentVariables.keySet().stream().filter(x -> x.startsWith(prefix)).collect(Collectors.toList());
	}

	private RuntimeException createEnvironmentVariableWrongUsageException() {
		return new RuntimeException("BaSyx environment variables should either use '.' or '_' as the separator, not both at the same time.");
	}

	private boolean nameContainsUppercase(String varName) {
		return varName.chars().anyMatch(c -> Character.isUpperCase(c));
	}

	private String changeToUnderscoreAndLowerCase(String variableName) {
		return variableName.toLowerCase().replace('.', '_');
	}

	/**
	 * Sets a property, if it is contained in this configuration
	 * 
	 * @param name
	 *            The name of the property
	 * @param value
	 *            The new value of the property
	 */
	public void setProperty(String name, String value) {
		values.put(name, value);
	}

	/**
	 * Returns all contained properties that begin with a specific prefix
	 * 
	 * @param prefix
	 *            The filtered prefix (e.g. "aas.")
	 * @return The list of all contained properties that begin with the prefix
	 */
	public List<String> getProperties(String prefix) {
		return values.keySet().stream().filter(key -> key.startsWith(prefix)).collect(Collectors.toList());
	}

	/**
	 * Queries a property
	 */
	public String getProperty(String name) {
		return values.get(name);
	}

	private static Map<String, String> classesBySimpleNameMap = new HashMap<>();

	static {
		classesBySimpleNameMap.put(KeycloakRoleAuthenticator.class.getSimpleName(), KeycloakRoleAuthenticator.class.getName());
		classesBySimpleNameMap.put(AuthenticationGrantedAuthorityAuthenticator.class.getSimpleName(), AuthenticationGrantedAuthorityAuthenticator.class.getName());
		classesBySimpleNameMap.put(JWTAuthenticationContextProvider.class.getSimpleName(), JWTAuthenticationContextProvider.class.getName());
		classesBySimpleNameMap.put(AuthenticationContextProvider.class.getSimpleName(), AuthenticationContextProvider.class.getName());
	}

	public <T> T loadInstanceDynamically(final String propertyName, final Class<T> requiredInterface) {
		try {
			final String className = getProperty(propertyName);
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);

			if (effectiveClassName == null) {
				throw new IllegalArgumentException("granted authority subject information provider class is null");
			}

			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!requiredInterface.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException(String.format("given %s -> %s does not implement the interface %s.", propertyName, effectiveClassName, requiredInterface.getName()));
			}

			return (T) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}
}
