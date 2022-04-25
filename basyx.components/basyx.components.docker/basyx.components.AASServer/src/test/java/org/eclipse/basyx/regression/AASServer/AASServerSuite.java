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
package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Suite for testing that the AAS Server component is set up correctly. The
 * tests here can be used by the component test itself and the integration test
 * 
 * @author espen
 *
 */
public abstract class AASServerSuite {
	protected IAASRegistry aasRegistry;
	protected ConnectedAssetAdministrationShellManager manager;

	protected IIdentifier shellIdentifier = new CustomId("shellId");
	protected IIdentifier submodelIdentifier = new CustomId("submodelId");

	protected abstract String getURL();

	@Before
	public void setUp() {
		// Create a dummy registry to test integration of XML AAS
		aasRegistry = new InMemoryRegistry();

		// Create ConnectedAASManager
		IConnectorFactory connectorFactory = new HTTPConnectorFactory();
		manager = new ConnectedAssetAdministrationShellManager(aasRegistry, connectorFactory);
	}

	@Test
	public void testAddAAS() throws Exception {
		AssetAdministrationShell shell = createShell(shellIdentifier.getId(), shellIdentifier);
		manager.createAAS(shell, getURL());

		IAssetAdministrationShell remote = manager.retrieveAAS(shellIdentifier);
		assertEquals(shell.getIdShort(), remote.getIdShort());
	}

	@Test
	public void testAddSubmodel() throws Exception {
		IIdentifier shellIdentifierForSubmodel = new CustomId("shellSubmodelId");
		AssetAdministrationShell shell = createShell(shellIdentifierForSubmodel.getId(), shellIdentifierForSubmodel);
		manager.createAAS(shell, getURL());

		Submodel submodel = createSubmodel(submodelIdentifier.getId(), submodelIdentifier);
		manager.createSubmodel(shellIdentifierForSubmodel, submodel);

		ISubmodel remote = manager.retrieveSubmodel(shellIdentifierForSubmodel, submodelIdentifier);
		assertEquals(submodel.getIdShort(), remote.getIdShort());
	}

	protected AssetAdministrationShell createShell(String idShort, IIdentifier identifier) {
		AssetAdministrationShell shell = new AssetAdministrationShell();
		shell.setIdentification(identifier);
		shell.setIdShort(idShort);
		return shell;
	}

	protected Submodel createSubmodel(String idShort, IIdentifier submodelIdentifier) {
		Submodel submodel = new Submodel();
		submodel.setIdentification(submodelIdentifier);
		submodel.setIdShort(idShort);
		return submodel;
	}
}
