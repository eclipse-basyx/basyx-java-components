/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.regression.AASServer.mongodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAPI;
import org.eclipse.basyx.components.configuration.BaSyxMongoDBConfiguration;
import org.eclipse.basyx.components.internal.mongodb.MongoDBBaSyxStorageAPI;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author jungjan, witt
 *
 */
public class TestMongoDBAAASAPI {
	private static MongoDBAASAPI shellAPI;
	private final static String COLLECTION_NAME = "testCollection";
	private final static BaSyxMongoDBConfiguration config = new BaSyxMongoDBConfiguration();
	private static MongoDBBaSyxStorageAPI<AssetAdministrationShell> mongoDBStorageAPI;
	private final static String SHELL_IDENTIFICATION_ID = "testIdentificationId";

	@SuppressWarnings("deprecation")
	@BeforeClass
	public static void setUpClass() {
		mongoDBStorageAPI = new MongoDBBaSyxStorageAPI<>(COLLECTION_NAME, AssetAdministrationShell.class, config);
		shellAPI = new MongoDBAASAPI(mongoDBStorageAPI, SHELL_IDENTIFICATION_ID);
	}

	@Before
	public void before() {
		Collection<AssetAdministrationShell> shells = mongoDBStorageAPI.retrieveAll();
		shells.forEach(shell -> mongoDBStorageAPI.delete(shell.getIdentification().getId()));
	}

	@Test
	public void setAndGetAAS() {
		String idShort = "testIdShort";

		IIdentifier identification = new CustomId(SHELL_IDENTIFICATION_ID);
		AssetAdministrationShell expectedShell = new AssetAdministrationShell(idShort, identification, null);

		shellAPI.setAAS(expectedShell);
		AssetAdministrationShell resultShell = (AssetAdministrationShell) shellAPI.getAAS();

		assertEquals(expectedShell, resultShell);
	}

	@Test
	public void addSubmodel() {
		String idShortShell = "testIdShortShell";
		String idShortSubmodel = "testIdShortSubmodel";

		IIdentifier identification = new CustomId(SHELL_IDENTIFICATION_ID);

		AssetAdministrationShell expectedShell = new AssetAdministrationShell(idShortShell, identification, null);
		shellAPI.setAAS(expectedShell);

		Submodel expectedSubmodel = new Submodel(idShortSubmodel, identification);
		IReference testReference = expectedSubmodel.getReference();

		expectedShell.addSubmodelReference(testReference);
		shellAPI.addSubmodel(testReference);

		Object resultShell = shellAPI.getAAS();

		assertEquals(expectedShell, resultShell);

	}

	@Test
	public void removeSubmodel() {
		String idShortShell = "testIdShortShell";
		String idShortSubmodel = "testIdShortSubmodel";

		IIdentifier identification = new CustomId(SHELL_IDENTIFICATION_ID);

		AssetAdministrationShell testShell = new AssetAdministrationShell(idShortShell, identification, null);

		Submodel expectedSubmodel = new Submodel(idShortSubmodel, identification);
		IReference testReference = expectedSubmodel.getReference();

		shellAPI.setAAS(testShell);
		shellAPI.addSubmodel(testReference);
		shellAPI.removeSubmodel(expectedSubmodel.getIdentification().getId());

		IAssetAdministrationShell resultShell = shellAPI.getAAS();
		Collection<IReference> submodelReferences = resultShell.getSubmodelReferences();
		assertTrue(submodelReferences.isEmpty());
	}
}
