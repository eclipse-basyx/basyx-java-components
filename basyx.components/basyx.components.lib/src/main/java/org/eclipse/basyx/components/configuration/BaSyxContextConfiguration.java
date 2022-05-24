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

import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.JwtBearerTokenAuthenticationConfiguration;

/**
 * Represents a BaSyx http servlet configuration for a BaSyxContext, that can be
 * loaded from a properties file.
 * 
 * @author espen
 *
 */
public class BaSyxContextConfiguration extends BaSyxConfiguration {
	// Prefix for environment variables
	public static final String ENV_PREFIX = "BaSyxContext_";

	// Default BaSyx Context configuration
	public static final String DEFAULT_CONTEXTPATH = "basys.sdk";
	public static final String DEFAULT_DOCBASE = System.getProperty("java.io.tmpdir");
	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final int DEFAULT_PORT = 4000;

	public static final String CONTEXTPATH = "contextPath";
	public static final String DOCBASE = "contextDocPath";
	public static final String HOSTNAME = "contextHostname";
	public static final String PORT = "contextPort";

	public static final String SSL_KEY_STORE_LOCATION = "sslKeyStoreLocation";
	public static final String SSL_KEY_PASSWORD = "sslKeyPass";

	public static final String JWT_BEARER_TOKEN_AUTHENTICATION_ISSUER_URI = "jwtBearerTokenAuthenticationIssuerUri";
	public static final String JWT_BEARER_TOKEN_AUTHENTICATION_JWK_SET_URI = "jwtBearerTokenAuthenticationJwkSetUri";
	public static final String JWT_BEARER_TOKEN_AUTHENTICATION_REQUIRED_AUD = "jwtBearerTokenAuthenticationRequiredAud";
	
	public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "accessControlAllowOrigin"; 

	// The default path for the context properties file
	public static final String DEFAULT_CONFIG_PATH = "context.properties";

	// The default key for variables pointing to the configuration file
	public static final String DEFAULT_FILE_KEY = "BASYX_CONTEXT";

	public static Map<String, String> getDefaultProperties() {
		Map<String, String> defaultProps = new HashMap<>();
		defaultProps.put(CONTEXTPATH, DEFAULT_CONTEXTPATH);
		defaultProps.put(DOCBASE, DEFAULT_DOCBASE);
		defaultProps.put(HOSTNAME, DEFAULT_HOSTNAME);
		defaultProps.put(PORT, Integer.toString(DEFAULT_PORT));
		defaultProps.put(SSL_KEY_STORE_LOCATION, null);
		defaultProps.put(SSL_KEY_PASSWORD, null);
		defaultProps.put(ACCESS_CONTROL_ALLOW_ORIGIN, null);

		return defaultProps;
	}

	/**
	 * Empty Constructor - use default values
	 */
	public BaSyxContextConfiguration() {
		super(getDefaultProperties());
	}

	/**
	 * Constructor with predefined value map
	 */
	public BaSyxContextConfiguration(Map<String, String> values) {
		super(values);
	}

	/**
	 * Constructor with initial configuration - docBasePath and hostname are default
	 * values
	 * 
	 * @param port
	 *            The port that will be occupied
	 * @param contextPath
	 *            The subpath for this context
	 */
	public BaSyxContextConfiguration(int port, String contextPath) {
		this();
		setPort(port);
		setContextPath(contextPath);
	}

	/**
	 * Constructor with initial configuration - docBasePath and hostname are default
	 * values
	 * 
	 * @param contextPath
	 *            The subpath for this context
	 * @param docBasePath
	 *            The local base path for the documents
	 * @param hostname
	 *            The hostname
	 * @param port
	 *            The port that will be occupied
	 */
	public BaSyxContextConfiguration(String contextPath, String docBasePath, String hostname, int port) {
		this();
		setContextPath(contextPath);
		setDocBasePath(docBasePath);
		setHostname(hostname);
		setPort(port);
	}

	public void loadFromEnvironmentVariables() {
		String[] properties = { CONTEXTPATH, DOCBASE, HOSTNAME, PORT, JWT_BEARER_TOKEN_AUTHENTICATION_ISSUER_URI, JWT_BEARER_TOKEN_AUTHENTICATION_JWK_SET_URI, JWT_BEARER_TOKEN_AUTHENTICATION_REQUIRED_AUD, ACCESS_CONTROL_ALLOW_ORIGIN };
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

	public void loadFromDefaultSource() {
		loadFileOrDefaultResource(DEFAULT_FILE_KEY, DEFAULT_CONFIG_PATH);
		loadFromEnvironmentVariables();
	}

	public BaSyxContext createBaSyxContext() {
		String reqContextPath = getContextPath();
		String reqDocBasePath = getDocBasePath();
		String hostName = getHostname();
		Boolean isSecuredCon = isSecureConnection();
		String sslKeyStoreLocation = getSSLKeyStoreLocation();
		String sslKeyPass = getSSLKeyPassword();
		int reqPort = getPort();

		final BaSyxContext baSyxContext;
		if (!isSecuredCon) {
			baSyxContext = new BaSyxContext(reqContextPath, reqDocBasePath, hostName, reqPort);
		} else {
			baSyxContext = new BaSyxContext(reqContextPath, reqDocBasePath, hostName, reqPort, isSecuredCon, sslKeyStoreLocation, sslKeyPass);
		}

		if (atLeastOneJwtPropertyIsSet()) {
			configureJwtAuthentication(baSyxContext);
		}
		
		enableCORSIfConfigured(baSyxContext);

		return baSyxContext;
	}

	private void enableCORSIfConfigured(final BaSyxContext baSyxContext) {
		if(!isCorsConfigured()) {
			return;
		}

		baSyxContext.setAccessControlAllowOrigin(getAccessControlAllowOrigin());
	}

	private boolean isCorsConfigured() {
		return getAccessControlAllowOrigin() != null && !getAccessControlAllowOrigin().isEmpty();
	}

	private Boolean isSecureConnection() {
		return (getSSLKeyStoreLocation() != null && getSSLKeyPassword() != null);
	}

	private boolean atLeastOneJwtPropertyIsSet() {
		return getJwtBearerTokenAuthenticationIssuerUri() != null || getJwtBearerTokenAuthenticationJwkSetUri() != null || getJwtBearerTokenAuthenticationRequiredAud() != null;
	}

	private void configureJwtAuthentication(final BaSyxContext baSyxContext) {
		baSyxContext.setJwtBearerTokenAuthenticationConfiguration(
				JwtBearerTokenAuthenticationConfiguration.of(getJwtBearerTokenAuthenticationIssuerUri(), getJwtBearerTokenAuthenticationJwkSetUri(), getJwtBearerTokenAuthenticationRequiredAud()));
	}

	public String getContextPath() {
		return getProperty(CONTEXTPATH);
	}

	public void setContextPath(String contextPath) {
		setProperty(CONTEXTPATH, VABPathTools.stripSlashes(contextPath));
	}

	public String getDocBasePath() {
		return getProperty(DOCBASE);
	}

	public void setDocBasePath(String docBasePath) {
		setProperty(DOCBASE, docBasePath);
	}

	public String getHostname() {
		return getProperty(HOSTNAME);
	}

	public void setHostname(String hostname) {
		setProperty(HOSTNAME, hostname);
	}

	public int getPort() {
		return Integer.parseInt(getProperty(PORT));
	}

	public void setPort(int port) {
		setProperty(PORT, Integer.toString(port));
	}

	public String getSSLKeyPassword() {
		return getProperty(SSL_KEY_PASSWORD);
	}

	public void setSSLKeyPassword(String sslKeyPass) {
		setProperty(SSL_KEY_PASSWORD, sslKeyPass);
	}

	public String getSSLKeyStoreLocation() {
		return getProperty(SSL_KEY_STORE_LOCATION);
	}

	public void setSSLKeyStoreLocation(String sslKeyStoreLocation) {
		setProperty(SSL_KEY_STORE_LOCATION, sslKeyStoreLocation);
	}

	public String getJwtBearerTokenAuthenticationIssuerUri() {
		return getProperty(JWT_BEARER_TOKEN_AUTHENTICATION_ISSUER_URI);
	}

	public void setJwtBearerTokenAuthenticationIssuerUri(String jwtBearerAuthIssuerUri) {
		setProperty(JWT_BEARER_TOKEN_AUTHENTICATION_ISSUER_URI, jwtBearerAuthIssuerUri);
	}

	public String getJwtBearerTokenAuthenticationJwkSetUri() {
		return getProperty(JWT_BEARER_TOKEN_AUTHENTICATION_JWK_SET_URI);
	}

	public void setJwtBearerTokenAuthenticationJwkSetUri(String jwtBearerAuthJwkSetUri) {
		setProperty(JWT_BEARER_TOKEN_AUTHENTICATION_JWK_SET_URI, jwtBearerAuthJwkSetUri);
	}

	public String getJwtBearerTokenAuthenticationRequiredAud() {
		return getProperty(JWT_BEARER_TOKEN_AUTHENTICATION_REQUIRED_AUD);
	}

	public void setJwtBearerTokenAuthenticationRequiredAud(String jwtBearerAuthRequiredAud) {
		setProperty(JWT_BEARER_TOKEN_AUTHENTICATION_REQUIRED_AUD, jwtBearerAuthRequiredAud);
	}
	
	public String getAccessControlAllowOrigin() { 
        return getProperty(ACCESS_CONTROL_ALLOW_ORIGIN); 
    }

    public void setAccessControlAllowOrigin(String accessControlAllowOrigin) { 
        setProperty(ACCESS_CONTROL_ALLOW_ORIGIN, accessControlAllowOrigin); 
    } 

	public String getUrl() {
		String contextPath = getContextPath();
		String base = getProtocol() + getHostname() + ":" + getPort();
		if (contextPath.isEmpty()) {
			return base;
		} else {
			return VABPathTools.concatenatePaths(base, contextPath);
		}
	}

	private String getProtocol() {
		if (isSecureConnection()) {
			return "https://";
		}
		return "http://";
	}
}
