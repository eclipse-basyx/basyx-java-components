/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.regression.registry;

import org.apache.commons.collections4.map.HashedMap;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.components.registry.authorization.AuthorizedTaggedDirectoryFactory;
import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.map.MapTaggedDirectory;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.testsuite.regression.extensions.shared.mqtt.AuthorizationContextProvider;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests authorization implementation for the AASTaggedDirectory
 *
 * @author fried
 */
public class TestTaggedDirectoryAuthorization {

	private static IAASTaggedDirectory authorizedTaggedDirectory;

	private AuthorizationContextProvider securityContextProvider = new AuthorizationContextProvider(AuthorizedAASRegistry.READ_AUTHORITY, AuthorizedAASRegistry.WRITE_AUTHORITY);

	@BeforeClass
	public static void setUp() {
		authorizedTaggedDirectory = new MapTaggedDirectory(new HashedMap<>(), new HashedMap<>());
		authorizedTaggedDirectory = new AuthorizedTaggedDirectoryFactory().create(authorizedTaggedDirectory);
	}

	@Test
	public void writeAction_securityContextWithWriteAuthority() {
		securityContextProvider.setSecurityContextWithWriteAuthority();
		AASDescriptor descriptor = createTestDescriptor();
		authorizedTaggedDirectory.register(descriptor);
	}

	@Test(expected = ProviderException.class)
	public void writeAction_emptySecurityContextThrowsError() {
		securityContextProvider.setEmptySecurityContext();
		AASDescriptor descriptor = createTestDescriptor();
		authorizedTaggedDirectory.register(descriptor);
	}

	@Test
	public void readAction_securityContextWithReadAuthority() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		authorizedTaggedDirectory.lookupAll();
	}

	@Test(expected = ProviderException.class)
	public void readAction_emptySecurityContextThrowsError() {
		securityContextProvider.setEmptySecurityContext();
		authorizedTaggedDirectory.lookupAll();
	}

	@Test(expected = ProviderException.class)
	public void readAction_LookupTagEmptySecurityContextThrowsError() {
		securityContextProvider.setEmptySecurityContext();
		authorizedTaggedDirectory.lookupTag("test");
	}

	@Test
	public void readAction_LookupTagWithReadAuthority() {
		securityContextProvider.setSecurityContextWithReadAuthority();
		authorizedTaggedDirectory.lookupTag("test");
	}

	private AASDescriptor createTestDescriptor() {
		Identifier shellIdentifier = new Identifier(IdentifierType.CUSTOM, "testIdentifier");
		String shellIdShort = "testIDShort";

		Asset shellAsset = new Asset("testAssetIdShort", new Identifier(IdentifierType.CUSTOM, "assetTestIdentifier"), AssetKind.INSTANCE);

		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell(shellIdShort, shellIdentifier, shellAsset);

		AASDescriptor descriptor = new AASDescriptor(assetAdministrationShell, "http://testEndpoint.test/testEndpoint");
		return descriptor;
	}

}
