/*******************************************************************************
* Copyright (C) 2021 the Eclipse BaSyx Authors
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/

*
* SPDX-License-Identifier: EPL-2.0
******************************************************************************/

package basyx.components.updater.aas.configuration.parser;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

import basyx.components.updater.aas.configuration.AASDatasinkConfiguration;
import basyx.components.updater.aas.configuration.BasyxInternalDatasourceConfiguration;
import basyx.components.updater.aas.configuration.BasyxInternalTransformerConfiguration;
import basyx.components.updater.core.configuration.route.configuration.RoutesConfiguration;
import basyx.components.updater.core.configuration.route.configuration.SimpleRouteConfiguration;

/**
 * An implementation of AAS Qualifer parser to grab the connection values from
 * the qualifier of an element
 *
 *
 */
public class AASQualifierParser {
	private IAssetAdministrationShell aas;
	private String aasEndpoint;

	private RoutesConfiguration config;

	/**
	 * Retrieves an instance of {@link AASQualifierParser}
	 *
	 * @param aas
	 * @param aasEndpoint
	 */
	public AASQualifierParser(IAssetAdministrationShell aas, String aasEndpoint) {
		this.aasEndpoint = aasEndpoint;
		this.aas = aas;
	}

	/**
	 * Retrieves new configuration from the AAS
	 *
	 * @return
	 */
	public RoutesConfiguration newConfiguration() {
		this.config = new RoutesConfiguration();
		extendConfiguration(config);
		return this.config;
	}

	/**
	 * Extends the {@link RoutesConfiguration} from the AAS
	 *
	 * @param config
	 */
	public void extendConfiguration(RoutesConfiguration config) {
		this.config = config;
		parseAAS(this.aas);
	}

	/**
	 * Parses the AAS
	 *
	 * @param aas
	 */
	private void parseAAS(IAssetAdministrationShell aas) {
		aas.getSubmodels().values().forEach(this::parseSubmodel);
	}

	/**
	 * Parses the submodels
	 *
	 * @param sm
	 */
	private void parseSubmodel(ISubmodel sm) {
		sm.getSubmodelElements().values().forEach(element -> {
			parseSMElement(element, sm.getIdShort());
		});
	}

	/**
	 * Parses the submodel elements recursively
	 *
	 * @param element
	 * @param parentPath
	 */
	private void parseSMElement(ISubmodelElement element, String parentPath) {
		if (element instanceof IProperty) {
			parseProperty((IProperty) element, parentPath);
		} else if (element instanceof ISubmodelElementCollection) {
			parseCollection((ISubmodelElementCollection) element, parentPath);
		}
	}

	/**
	 * Parses the collection
	 *
	 * @param collection
	 * @param parentPath
	 */
	private void parseCollection(ISubmodelElementCollection collection, String parentPath) {
		String idShort = collection.getIdShort();
		String currentPath = VABPathTools.concatenatePaths(parentPath, idShort);
		collection.getSubmodelElements().values().forEach(value -> {
			if (value instanceof ISubmodelElement) {
				parseSMElement(value, currentPath);
			}
		});
	}

	/**
	 * Parses the property
	 *
	 * @param property
	 * @param parentPath
	 */
	private void parseProperty(IProperty property, String parentPath) {
		if (!isPropertyConnected(property)) {
			return;
		}

		String propPath = VABPathTools.concatenatePaths(parentPath, property.getIdShort());
		String uniqueId = parentPath + "." + property.getIdShort();

		BasyxInternalDatasourceConfiguration datasourceConfig = createDatasourceConfigFromProperty(property);
		AASDatasinkConfiguration datasinkConfig = createDatasinkConfigFromProperty(propPath);
		BasyxInternalTransformerConfiguration transformerConfig = createTransformerConfigFromProperty(property);

		extendConfiguration(uniqueId, datasourceConfig, datasinkConfig, transformerConfig);
	}

	/**
	 * Extends the configuration from retrieved configuration
	 *
	 * @param uniqueId
	 * @param datasourceConfig
	 * @param datasinkConfig
	 * @param transformerConfig
	 */
	private void extendConfiguration(String uniqueId, BasyxInternalDatasourceConfiguration datasourceConfig, AASDatasinkConfiguration datasinkConfig, BasyxInternalTransformerConfiguration transformerConfig) {
		config.getDatasources().put(uniqueId, datasourceConfig);
		config.getDatasinks().put(uniqueId, datasinkConfig);
		if (transformerConfig != null) {
			config.getTransformers().put(uniqueId, transformerConfig);
		}

		SimpleRouteConfiguration routeConfig = createRouteConfiguration(uniqueId, transformerConfig);

		config.getRoutes().add(routeConfig);
	}

	/**
	 * Creates a new route configuration
	 *
	 * @param uniqueId
	 * @param transformerConfig
	 * @return
	 */
	private SimpleRouteConfiguration createRouteConfiguration(String uniqueId, BasyxInternalTransformerConfiguration transformerConfig) {
		SimpleRouteConfiguration routeConfig = new SimpleRouteConfiguration();
		routeConfig.setDatasource(uniqueId);
		routeConfig.setDatasink(uniqueId);
		if (transformerConfig != null) {
			routeConfig.getTransformers().add(uniqueId);
		}
		return routeConfig;
	}

	/**
	 * Checks whether the property is connected or not
	 *
	 * @param property
	 * @return
	 */
	private boolean isPropertyConnected(IProperty property) {
		return QualifierDatasourceParserFactory.hasDatasourceDefinition(property);
	}

	private BasyxInternalTransformerConfiguration createTransformerConfigFromProperty(IProperty property) {
		// TODO: How to create the transformer configs
		return null;
	}

	/**
	 * Creates data sink configuration from property
	 *
	 * @param parentPath
	 * @return
	 */
	private AASDatasinkConfiguration createDatasinkConfigFromProperty(String parentPath) {
		return new AASDatasinkConfiguration(aasEndpoint, parentPath);
	}

	/**
	 * Creates data source configuration from property
	 *
	 * @param property
	 * @return
	 */
	private BasyxInternalDatasourceConfiguration createDatasourceConfigFromProperty(IProperty property) {
		IQualifierDatasourceParser sinkParser = QualifierDatasourceParserFactory.createParser(property);
		return sinkParser.parseDatasourceConfiguration();
	}
}
