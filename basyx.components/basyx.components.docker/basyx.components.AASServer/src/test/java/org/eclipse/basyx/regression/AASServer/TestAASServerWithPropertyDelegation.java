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

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import java.util.Collections;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.testsuite.regression.extensions.submodel.delegation.DelegationTestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;

/**
 * Integration tests for Submodel with delegated Property
 *
 * @author danish
 *
 */
public class TestAASServerWithPropertyDelegation {
	private static AASServerComponent aasServerComponent;
	
	private static final String SM_ELEM_IDSHORT = "delegated";
	
	private static IIdentifier aasIdentifier = new CustomId("testAAS");
	private static IIdentifier smIdentifier = new CustomId("testSM");
	private static ClientAndServer mockServer;
	private static MockServerClient mockServerClient;
	
	@BeforeClass
	public static void init() {
		configureAndStartMockHttpServer();
		
		AASBundle aasBundle = createAASBundle();
		
		mockServerClient = DelegationTestHelper.createExpectationForMockedGet();
		
		startAASServerComponentWithAASBundle(configureAndGetBasyxContext(), new BaSyxAASServerConfiguration(), aasBundle);
	}
	
	@Test
	public void valueFromEndpointOfDelegatedPropertyIsReceived() {
		IAASAggregator proxy = new AASAggregatorProxy(aasServerComponent.getURL());
		
		Object actualValue = proxy.getAAS(aasIdentifier).getSubmodel(smIdentifier).getSubmodelElement(SM_ELEM_IDSHORT).getValue();
		
		assertEquals(DelegationTestHelper.EXPECTED_VALUE, Integer.parseInt(actualValue.toString()));
	}

	private static AASBundle createAASBundle() {
		String submodelIdShort = "testSubmodel";
		
		Property delegatedProperty = DelegationTestHelper.createDelegatedProperty();
		
		Submodel submodel = new Submodel(submodelIdShort, smIdentifier);
		submodel.addSubmodelElement(delegatedProperty);
		
		AssetAdministrationShell aas = createAssetAdministrationShell();
		aas.addSubmodel(submodel);
		
		return new AASBundle(aas, Collections.singleton(submodel));
	}

	private static AssetAdministrationShell createAssetAdministrationShell() {
		String aasIdShort = "test";
		
		AssetAdministrationShell  aas = new AssetAdministrationShell();
		aas.setIdentification(aasIdentifier);
		aas.setIdShort(aasIdShort);
		
		return aas;
	}

	private static void configureAndStartMockHttpServer() {
		mockServer = startClientAndServer(DelegationTestHelper.SERVER_PORT);
	}
	
	private static void startAASServerComponentWithAASBundle(BaSyxContextConfiguration contextConfig,
			BaSyxAASServerConfiguration aasContextConfig, AASBundle aasBundle) {
		aasServerComponent = new AASServerComponent(contextConfig, aasContextConfig);
		
		aasServerComponent.setAASBundle(aasBundle);
		
		aasServerComponent.startComponent();
	}
	
	private static BaSyxContextConfiguration configureAndGetBasyxContext() {
		String hostname = "localhost";
		String contextPath = "/aasServer";
		
		BaSyxContextConfiguration contextConfig= new BaSyxContextConfiguration();
		contextConfig.setHostname(hostname);
		contextConfig.setContextPath(contextPath);
		contextConfig.setPort(4001);
		
		return contextConfig;
	}
	
	@AfterClass
    public static void stopServer() {
		aasServerComponent.stopComponent();
		
        mockServer.stop();
        
        mockServerClient.close();
    }
}
