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

import basyx.components.updater.aas.configuration.BasyxInternalDatasourceConfiguration;

/**
 * A generic interface of Qualifier data source parser
 *
 */
public interface IQualifierDatasourceParser {
	
	/**
	 * Parses data source configuration
	 * @return
	 */
	public BasyxInternalDatasourceConfiguration parseDatasourceConfiguration();
}
