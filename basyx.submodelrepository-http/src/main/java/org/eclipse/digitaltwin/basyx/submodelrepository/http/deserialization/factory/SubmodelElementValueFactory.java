package org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.factory;

import org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.util.SubmodelElementValueUtil;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValueType;
import com.fasterxml.jackson.databind.JsonNode;

public class SubmodelElementValueFactory {

	public SubmodelElementValue create(SubmodelElementValueType submodelElementValueType, JsonNode node) {
		switch (submodelElementValueType) {
		case RANGE:
			return SubmodelElementValueUtil.createRangeValue(node);
		case MULTI_LANGUAGE_PROPERTY_VALUE:
			return SubmodelElementValueUtil.createMultiLanguagePropertyValue(node);
		case PROPERTY:
			return SubmodelElementValueUtil.createPropertyValue(node);
		case FILE:
			return SubmodelElementValueUtil.createFileValue(node);
		default:
			throw new IllegalArgumentException("Unsupported type: " + submodelElementValueType);
		}
	}
}
