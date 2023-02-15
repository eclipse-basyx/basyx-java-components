package org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.factory;

import org.eclipse.digitaltwin.basyx.submodelservice.value.FileValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.util.SubmodelElementValueDeserializationUtil.*;

public class SubmodelElementValueDeserializationFactory {

	public SubmodelElementValue create(ObjectMapper mapper, JsonNode node) {
		if (isTypeOfRangeValue(node)) {
			return mapper.convertValue(node, RangeValue.class);
        } else if (node.isArray()) {
            return createMultiLanguagePropertyValue(node);
        } else if (isTypeOfFileValue(node)) {
        	return mapper.convertValue(node, FileValue.class);
        } else if(isTypeOfPropertyValue(node)) {
        	return mapper.convertValue(node, PropertyValue.class);
        }
		
		throw new IllegalArgumentException("Unsupported type: ");
	}
}
