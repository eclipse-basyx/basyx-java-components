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

import java.util.Map;

import basyx.components.updater.aas.configuration.BasyxInternalDatasourceConfiguration;
import basyx.components.updater.aas.configuration.MQTTDatasourceConfiguration;

/**
 * An implementation of Qualifier parser which parses MQTT connection
 * string from the qualifier
 * @author haque
 *
 */
public class MQTTQualifierParser implements IQualifierDatasourceParser {
	public static final String BROKER_ENDPOINT = "BaSyxMQTTBrokerEndpoint";
	public static final String TOPIC = "BaSyxMQTTTopic";

	private Map<String, String> qualifierMap;

	public MQTTQualifierParser(Map<String, String> qualifierMap) {
		this.qualifierMap = qualifierMap;
	}

	@Override
	public BasyxInternalDatasourceConfiguration parseDatasourceConfiguration() {
		MQTTDatasourceConfiguration dsConfiguration = new MQTTDatasourceConfiguration();
		dsConfiguration.setType(MQTTDatasourceConfiguration.TYPE);
		dsConfiguration.setServerUrl(qualifierMap.get(BROKER_ENDPOINT));
		dsConfiguration.setTopic(qualifierMap.get(TOPIC));
		return dsConfiguration;
	}

}
