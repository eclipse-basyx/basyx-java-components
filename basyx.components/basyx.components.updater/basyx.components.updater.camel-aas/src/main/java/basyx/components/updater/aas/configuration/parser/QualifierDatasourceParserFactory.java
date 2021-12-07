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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifiable;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifier;

/**
 * A factory implementation of data source 
 * parser based on data source type
 * 
 * @author haque
 *
 */
public class QualifierDatasourceParserFactory {
	public static final String DATASOURCE_TYPE = "BaSyxDatasourceType";

	public QualifierDatasourceParserFactory() {
	}

	/**
	 * Creates a qualifier parser from {@link IQualifiable}
	 * @param qualifiable
	 * @return
	 */
	public static IQualifierDatasourceParser createParser(IQualifiable qualifiable) {
		Collection<IConstraint> qualifiers = qualifiable.getQualifiers();
		Map<String, String> qualifierMap = convertQualifiersToMap(qualifiers);
		return createParser(qualifierMap);
	}

	/**
	 * Checks whether the qualifiable has data source definition
	 * @param qualifiable
	 * @return
	 */
	public static boolean hasDatasourceDefinition(IQualifiable qualifiable) {
		Collection<IConstraint> qualifiers = qualifiable.getQualifiers();
		Map<String, String> qualifierMap = convertQualifiersToMap(qualifiers);
		return qualifierMap.containsKey(DATASOURCE_TYPE);
	}

	/**
	 * Creates a data source parser based on DATASOURCE_TYPE
	 * @param qualifierMap
	 * @return
	 */
	private static IQualifierDatasourceParser createParser(Map<String, String> qualifierMap) {
		if (qualifierMap.get(DATASOURCE_TYPE).equals("MQTT")) {
			return new MQTTQualifierParser(qualifierMap);
		}
		return null;
	}

	/**
	 * Converts the qualifiers to map
	 * @param qualifiers
	 * @return
	 */
	private static Map<String, String> convertQualifiersToMap(Collection<IConstraint> qualifiers) {
		Map<String, String> qualifierMap = new HashMap<>();
		for (IConstraint constraint : qualifiers) {
			if (constraint instanceof IQualifier) {
				IQualifier qualifier = (IQualifier) constraint;
				String type = qualifier.getType();
				String value = (String) qualifier.getValue();
				qualifierMap.put(type, value);
			}
		}
		return qualifierMap;
	}
}
