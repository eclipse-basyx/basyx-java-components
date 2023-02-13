package org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.factory;

import org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.util.SubmodelElementValueDeserializationUtil;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValueType;
import com.fasterxml.jackson.databind.JsonNode;

public class SubmodelElementValueDeserializationFactory {

	public SubmodelElementValue create(SubmodelElementValueType submodelElementValueType, JsonNode node) {
		switch (submodelElementValueType) {
		case RANGE:
			return SubmodelElementValueDeserializationUtil.createRangeValue(node);
		case MULTI_LANGUAGE_PROPERTY_VALUE:
			return SubmodelElementValueDeserializationUtil.createMultiLanguagePropertyValue(node);
		case PROPERTY:
			return SubmodelElementValueDeserializationUtil.createPropertyValue(node);
		case FILE:
			return SubmodelElementValueDeserializationUtil.createFileValue(node);
		default:
			throw new IllegalArgumentException("Unsupported type: " + submodelElementValueType);
		}
	}
}
