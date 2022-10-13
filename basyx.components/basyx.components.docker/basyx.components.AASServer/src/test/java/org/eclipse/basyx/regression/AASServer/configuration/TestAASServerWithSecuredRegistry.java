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
package org.eclipse.basyx.regression.AASServer.configuration;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.components.IComponent;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.configuration.exception.AuthorizationDisabledException;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.eclipse.basyx.extensions.aas.manager.authorized.AuthorizedConnectedAASManager;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistryProxy;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import com.tngtech.keycloakmock.api.KeycloakMock;

/**
 * Tests AAS Server communication with authorized registry
 *
 * @author danish
 */
@Ignore("The requested changes in Keycloak Mock server is pending")
public class TestAASServerWithSecuredRegistry {
	private static final String AAS_IDSHORT = "TestAAS";
	private static String REGISTRYPATH = "http://localhost:4000/registry";
	private static String AASSERVERPATH = "http://localhost:4001/aasServer";
	
	private static KeycloakMock keyCloakMock;
	
	private static AASServerComponent aasServerComponent;
	private static IComponent registryComponent;
	private static AuthorizedConnectedAASManager manager;
	private static AssetAdministrationShell assetAdministrationShell;
	private static Set<String> clientScopes;
	private final String REMOVED_SCOPE = "urn:org.eclipse.basyx:scope:aas-registry:read";
	
	@BeforeClass
	public static void init() {
		prepareClientScopes();
	}
	
	@Test
	public void retrieveAASFromAuthorizedRegistryUsingAuthorizedManager() {
		configureAndStartServices();
		
		assetAdministrationShell = createAASAndSubmodel(manager);
		
		String aasIdshort = manager.retrieveAAS(assetAdministrationShell.getIdentification()).getIdShort();
		
		assertEquals(AAS_IDSHORT, aasIdshort);
	}
	
	@Test(expected = ProviderException.class)
	public void exceptionShouldBeThrownWhenNotProvidingRequiredClientScope() {
		removeAClientScope();
		
		configureAndStartServices();
		
		assetAdministrationShell = createAASAndSubmodel(manager);
		
		manager.retrieveAAS(assetAdministrationShell.getIdentification()).getIdShort();
	}
	
	@Test(expected = AuthorizationDisabledException.class)
	public void exceptionShouldBeThrownWhenAuthorizationIsDisabled() {
		startRegistryServer();
		
		configureAndStartKeyCloakMockServer(clientScopes);
		
		BaSyxAASServerConfiguration aasContextConfig = configureAndStartAASServerComponent();
		aasContextConfig.disableAuthorization();
		
		createAuthorizedAASRegistryProxy(aasContextConfig);
	}
	
	@After
	public void stop() {
		stopServices();
		
		addAClientScopeIfNotPresent();
	}
	
	private static void prepareClientScopes() {
		clientScopes = new HashSet<String>();
		clientScopes.add("urn:org.eclipse.basyx:scope:aas-registry:read");
		clientScopes.add("urn:org.eclipse.basyx:scope:aas-registry:write");
		clientScopes.add("urn:org.eclipse.basyx:scope:aas-aggregator:read");
		clientScopes.add("urn:org.eclipse.basyx:scope:aas-aggregator:write");
		clientScopes.add("urn:org.eclipse.basyx:scope:aas-api:read");
		clientScopes.add("urn:org.eclipse.basyx:scope:aas-api:write");
		clientScopes.add("urn:org.eclipse.basyx:scope:sm-aggregator:read");
		clientScopes.add("urn:org.eclipse.basyx:scope:sm-aggregator:write");
		clientScopes.add("urn:org.eclipse.basyx:scope:sm-api:read");
		clientScopes.add("urn:org.eclipse.basyx:scope:sm-api:write");
		clientScopes.add("aas-aggregator");
		clientScopes.add("sm-aggregator");
		clientScopes.add("aas-registry");
		clientScopes.add("sm-api");
		clientScopes.add("aas-api");
	}

	private void addAClientScopeIfNotPresent() {
		if(clientScopes.contains(REMOVED_SCOPE)) {
			return;
		}
		
		clientScopes.add(REMOVED_SCOPE);
	}
	
	private void removeAClientScope() {
		clientScopes.remove(REMOVED_SCOPE);
	}

	private static AssetAdministrationShell createAASAndSubmodel(AuthorizedConnectedAASManager manager) {
		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell(AAS_IDSHORT, new Identifier(IdentifierType.CUSTOM, "CustomTestAAS"), new Asset());
		
		Submodel submodel = new Submodel("TestSubmodel", new Identifier(IdentifierType.CUSTOM, "CustomTestSubmodel"));
		
		assetAdministrationShell.addSubmodel(submodel);
		
		manager.createAAS(assetAdministrationShell, AASSERVERPATH);
		manager.createSubmodel(assetAdministrationShell.getIdentification(), submodel);
		
		return assetAdministrationShell;
	}
	
	private static void configureAndStartServices() {
		startRegistryServer();
		
		configureAndStartKeyCloakMockServer(clientScopes);
		
		BaSyxAASServerConfiguration aasContextConfig = configureAndStartAASServerComponent();
		
		IAASRegistry registry = createAuthorizedAASRegistryProxy(aasContextConfig);
		manager = createAuthorizedConnectedAASManager(aasContextConfig, registry);
	}
	
	private static void startRegistryServer() {
		BaSyxContextConfiguration contextConfig= new BaSyxContextConfiguration();
		contextConfig.loadFromResource("registryContext.properties");
		
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);
		registryConfig.enableAuthorization();
		
		registryComponent = new RegistryComponent(contextConfig, registryConfig);
		registryComponent.startComponent();
	}
	
	private static void configureAndStartKeyCloakMockServer(Set<String> scopes) {
		/* The below instantiation of keyCloakMock is commented because the changes in Keycloak mock is pending, 
		 * once the changes is merged uncomment this and delete the null declaration of keyCloakMock*/
		
		// keyCloakMock = new KeycloakMock(aServerConfig().withPort(9006).withDefaultRealm("basyx-demo").withClientScopes(scopes).build());
		
		keyCloakMock = null;
		
	    keyCloakMock.start();
	}

	private static BaSyxAASServerConfiguration configureAndStartAASServerComponent() {
		BaSyxContextConfiguration contextConfig= new BaSyxContextConfiguration();
		contextConfig.loadFromResource("aasContext.properties");
		
		BaSyxAASServerConfiguration aasContextConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "", REGISTRYPATH);
		aasContextConfig.loadFromResource("aasServerConfig.properties");
		
		aasServerComponent = new AASServerComponent(contextConfig ,aasContextConfig);
		aasServerComponent.startComponent();
		
		return aasContextConfig;
	}
	
	private static AuthorizedConnectedAASManager createAuthorizedConnectedAASManager(
			BaSyxAASServerConfiguration aasContextConfig, IAASRegistry registry) {
		return new AuthorizedConnectedAASManager(registry, aasContextConfig.configureAndGetAuthorizationSupplier());
	}

	private static IAASRegistry createAuthorizedAASRegistryProxy(BaSyxAASServerConfiguration aasContextConfig) {
		return new AuthorizedAASRegistryProxy(REGISTRYPATH, aasContextConfig.configureAndGetAuthorizationSupplier());
	}

	private static void stopServices() {
		keyCloakMock.stop();
		
		aasServerComponent.stopComponent();
		
		registryComponent.stopComponent();
	}
}
