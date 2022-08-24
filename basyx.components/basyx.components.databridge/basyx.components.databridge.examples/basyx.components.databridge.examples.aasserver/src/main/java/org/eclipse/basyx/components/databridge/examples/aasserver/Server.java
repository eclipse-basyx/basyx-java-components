/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.databridge.examples.aasserver;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;

public class Server {
	// Server URLs
	public static final String REGISTRYPATH = "http://localhost:4000/registry";
	public static final String AASSERVERPATH = "http://localhost:4001/aasServer";

	// AAS/Submodel/Property Ids
	public static final IIdentifier OVENAASID = new CustomId("eclipse.basyx.aas.oven");
	public static final IIdentifier DOCUSMID = new CustomId("eclipse.basyx.submodel.documentation");
	public static final String MAXTEMPID = "maxTemp";

	public static void main(String[] args) {
		// Create Infrastructure
		startRegistry();
		startAASServer();

		// Create Manager - This manager is used to interact with an AAS server
		ConnectedAssetAdministrationShellManager manager = 
				new ConnectedAssetAdministrationShellManager(new AASRegistryProxy(REGISTRYPATH));
		
		// Create AAS and push it to server
		Asset asset = new Asset("ovenAsset", new CustomId("eclipse.basyx.asset.oven"), AssetKind.INSTANCE);
		AssetAdministrationShell shell = new AssetAdministrationShell("oven", OVENAASID, asset);
		
		// The manager uploads the AAS and registers it in the Registry server
		manager.createAAS(shell, AASSERVERPATH);
		
		// Create submodel
		Submodel documentationSubmodel = new Submodel("documentationSm", DOCUSMID);

		// - Create property
		Property maxTemp = new Property(MAXTEMPID, 1000);

		// Add the property to the Submodel
		documentationSubmodel.addSubmodelElement(maxTemp);

		// - Push the Submodel to the AAS server
		manager.createSubmodel(shell.getIdentification(), documentationSubmodel);
	}

	/**
	 * Starts an empty registry at "http://localhost:4000"
	 */
	private static void startRegistry() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(4000, "/registry");
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);
		RegistryComponent registry = new RegistryComponent(contextConfig, registryConfig);

		// Start the created server
		registry.startComponent();
	}

	/**
	 * Startup an empty server at "http://localhost:4001/"
	 */
	private static void startAASServer() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration(4001, "/aasServer");
		BaSyxAASServerConfiguration aasServerConfig = new BaSyxAASServerConfiguration(AASServerBackend.INMEMORY, "", REGISTRYPATH);
		AASServerComponent aasServer = new AASServerComponent(contextConfig, aasServerConfig);

		// Start the created server
		aasServer.startComponent();
	}
}
