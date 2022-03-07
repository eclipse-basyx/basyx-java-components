/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.AASServer.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.After;
import org.junit.Test;

/**
 * Tests 
 * Multiple serialization of AAS and
 * if AASServerComponent correctly deregisters automatically registered AASs/SMs
 * 
 * @author conradi
 *
 */
public class TestBaSyxAASServerConfigurationPropertyFileParsing {
	private static final String MULTIPLE_DIFFERENT_AAS_SERIALIZATION = "aas_multiple_different_source.properties";
	private static final String SINGLE_JSON_AAS_SERIALIZATION = "aas_single_json_source.properties";
	private static final String SINGLE_AAS_SERIALIZATION = "aas_single_source.properties";

	
	private static AASServerComponent component;
	private static InMemoryRegistry registry;
	
	private static void setUp(String resourcePath) {
		// Setup component's test configuration
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(8080, "");
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration();
		aasConfig.loadFromResource(resourcePath);
		
		// Create and start AASServer component
		component = new AASServerComponent(contextConfig, aasConfig);
		registry = new InMemoryRegistry();
		component.setRegistry(registry);
		component.startComponent();
	}
	
	@Test
	public void checkMultipleSerializedAasSourceOfDifferentTypes() {
		setUp(MULTIPLE_DIFFERENT_AAS_SERIALIZATION);
		
		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(4, aasDescriptors.size());
	}
	
	@Test
	public void checkSingleSerializedAasJsonSource() {
		setUp(SINGLE_JSON_AAS_SERIALIZATION);
		
		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(1, aasDescriptors.size());
	}
	
	@Test
	public void checkAasSerializedSouceDefinedWithoutJsonArray() {
		setUp(SINGLE_AAS_SERIALIZATION);
		
		List<AASDescriptor> aasDescriptors = registry.lookupAll();
		assertEquals(2, aasDescriptors.size());
	}
	
	@After
	public void stopAASServerComponent() {
		component.stopComponent();
	}
}
