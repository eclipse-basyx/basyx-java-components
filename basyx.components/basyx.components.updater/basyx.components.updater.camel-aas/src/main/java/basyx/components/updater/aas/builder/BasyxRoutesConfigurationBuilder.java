/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
* 
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

* 
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.aas.builder;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

import basyx.components.updater.aas.configuration.parser.AASQualifierParser;
import basyx.components.updater.core.configuration.route.configuration.RoutesConfiguration;

/**
 * An implementation of Basyx RoutesConfiguration builder
 * from internal XML schema
 * 
 */
public class BasyxRoutesConfigurationBuilder {
	private IAASRegistry registry;
	private IIdentifier aasId;

	/**
	 * Retrieves an instance of {@link BasyxRoutesConfigurationBuilder}
	 * @param registry
	 * @param aasId
	 */
	public BasyxRoutesConfigurationBuilder(IAASRegistry registry, IIdentifier aasId) {
		this.registry = registry;
		this.aasId = aasId;
	}

	/**
	 * Retrieves all routes configuration from AAS
	 * @return
	 * @throws Exception
	 */
	public RoutesConfiguration getRoutesConfiguration() throws Exception {
		AASQualifierParser configParser = createQualifierParser();
		RoutesConfiguration configuration = configParser.newConfiguration();
		return configuration;
	}

	/**
	 * Creates a parser for qualifier to get the connection values
	 * @return
	 */
	private AASQualifierParser createQualifierParser() {
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);
		ConnectedAssetAdministrationShell targetAAS = manager.retrieveAAS(aasId);
		AASDescriptor aasDesc = registry.lookupAAS(aasId);
		String aasEndpoint = aasDesc.getFirstEndpoint();
		return new AASQualifierParser(targetAAS, aasEndpoint);
	}
}
