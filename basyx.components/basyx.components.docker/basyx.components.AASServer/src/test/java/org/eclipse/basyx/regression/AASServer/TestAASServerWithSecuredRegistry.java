/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertFalse;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.components.IComponent;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.components.configuration.exception.AuthorizationConfigurationException;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.eclipse.basyx.extensions.aas.registration.authorization.AASRegistryScopes;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistryProxy;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.keycloakmock.api.KeycloakMock;

/**
 * Tests AAS Server communication with authorized registry
 *
 * @author danish
 */
@Ignore("The requested changes in Keycloak Mock server is pending")
public class TestAASServerWithSecuredRegistry {
	private static Logger logger = LoggerFactory.getLogger(TestAASServerWithSecuredRegistry.class);
	
	private static String REGISTRYPATH = "http://localhost:4000/registry";
	
	private static KeycloakMock keyCloakMock;
	private static IAASRegistry registry;
	private static AASServerComponent aasServerComponent;
	private static IComponent registryComponent;
	private static Set<String> clientScopes;
	
	@BeforeClass
	public static void init() {
		prepareClientScopes();
	}
	
	@Test
	public void retrieveAASFromAuthorizedRegistryUsingAuthorizedManager() {
		configureAndStartServices();
		
		assertFalse(registry.lookupAll().isEmpty());
	}
	
	@Test(expected = ProviderException.class)
	public void exceptionThrownWhenNotProvidingRequiredClientScope() {
		removeAClientScope();
		
		configureAndStartServices();
	}
	
	@Test(expected = AuthorizationConfigurationException.class)
	public void exceptionThrownWhenAuthorizationCredentialsForSecuredRegistryIsNotConfigured() {
		startRegistryServer();
		
		configureAndStartKeyCloakMockServer(clientScopes);
		
		BaSyxAASServerConfiguration aasContextConfig = configureAASServer();
		aasContextConfig.setTokenEndpoint("");
		
		aasContextConfig.configureAndGetAuthorizationSupplier();
	}
	
	@After
	public void stop() {
		stopServices();
		
		addAClientScopeIfNotPresent();
	}
	
	private static void prepareClientScopes() {
		clientScopes = new HashSet<String>();
		clientScopes.add(AASRegistryScopes.READ_SCOPE);
		clientScopes.add(AASRegistryScopes.WRITE_SCOPE);
	}

	private void addAClientScopeIfNotPresent() {
		if(clientScopes.contains(AASRegistryScopes.READ_SCOPE)) {
			return;
		}
		
		clientScopes.add(AASRegistryScopes.READ_SCOPE);
	}
	
	private void removeAClientScope() {
		clientScopes.remove(AASRegistryScopes.READ_SCOPE);
	}
	
	private static void configureAndStartServices() {
		startRegistryServer();
		
		configureAndStartKeyCloakMockServer(clientScopes);
		
		BaSyxAASServerConfiguration aasContextConfig = configureAASServer();
		
		startAASServerComponent(configureBasyxContext("aasContext.properties"), aasContextConfig);
		
		registry = createAuthorizedAASRegistryProxy(aasContextConfig);
	}
	
	private static void startRegistryServer() {
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);
		BaSyxSecurityConfiguration securityConfig = new BaSyxSecurityConfiguration(Collections.singletonMap(BaSyxSecurityConfiguration.AUTHORIZATION, BaSyxSecurityConfiguration.FEATURE_ENABLED));
		securityConfig.enableAuthorization();
		
		registryComponent = new RegistryComponent(configureBasyxContext("registryContext.properties"), registryConfig);
		registryComponent.startComponent();
	}
	
	private static void configureAndStartKeyCloakMockServer(Set<String> scopes) {
		/* The below instantiation of keyCloakMock is commented because the changes in Keycloak mock is pending, 
		 * once the changes is merged uncomment this and delete the null declaration of keyCloakMock*/
		
//		keyCloakMock = new KeycloakMock(aServerConfig().withPort(9006).withDefaultRealm("basyx-demo").withClientScopes(scopes).build());
		keyCloakMock = null;
		
	    keyCloakMock.start();
	}

	private static void startAASServerComponent(BaSyxContextConfiguration contextConfig,
			BaSyxAASServerConfiguration aasContextConfig) {
		aasServerComponent = new AASServerComponent(contextConfig, aasContextConfig);
		aasServerComponent.startComponent();
	}

	private static BaSyxContextConfiguration configureBasyxContext(String path) {
		BaSyxContextConfiguration contextConfig= new BaSyxContextConfiguration();
		contextConfig.loadFromResource(path);
		return contextConfig;
	}

	private static BaSyxAASServerConfiguration configureAASServer() {
		BaSyxAASServerConfiguration aasContextConfig = new BaSyxAASServerConfiguration();
		aasContextConfig.loadFromResource("aasServerConfig.properties");
		return aasContextConfig;
	}

	private static IAASRegistry createAuthorizedAASRegistryProxy(BaSyxAASServerConfiguration aasContextConfig) {
		return new AuthorizedAASRegistryProxy(REGISTRYPATH, aasContextConfig.configureAndGetAuthorizationSupplier());
	}

	private static void stopServices() {
		try {
			aasServerComponent.stopComponent();
		} catch (Exception e) {
			logger.debug("Could not stop AAS Server Component");
		}
		
		registryComponent.stopComponent();
		
		keyCloakMock.stop();
	}
}
