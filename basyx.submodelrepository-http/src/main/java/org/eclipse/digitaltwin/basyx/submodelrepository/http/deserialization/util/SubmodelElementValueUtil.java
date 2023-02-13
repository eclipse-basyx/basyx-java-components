package org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.util;

import org.eclipse.digitaltwin.basyx.submodelservice.value.FileValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

import com.fasterxml.jackson.databind.JsonNode;

public class SubmodelElementValueUtil {
	
	private static final String CONTENT_TYPE = "contentType";
	private static final String MAX = "max";
	private static final String MIN = "min";
	private static final String VALUE = "value";

	private SubmodelElementValueUtil() {
	    throw new IllegalStateException("Utility class");
	  }
	
	public static SubmodelElementValue createPropertyValue(JsonNode node) {
		String value = node.get(VALUE).asText();
		
		return new PropertyValue(value);
	}
	
	public static SubmodelElementValue createRangeValue(JsonNode node) {
		int min = node.get(MIN).asInt();
		int max = node.get(MAX).asInt();
		
		return new RangeValue(min, max);
	}
	
	public static SubmodelElementValue createMultiLanguagePropertyValue(JsonNode node) {
		String contentType1 = node.get(CONTENT_TYPE).asText();
		String fileValue2 = node.get(VALUE).asText();
		
		return new FileValue(contentType1, fileValue2);
	}
	
	public static SubmodelElementValue createFileValue(JsonNode node) {
		String contentType = node.get(CONTENT_TYPE).asText();
		String fileValue = node.get(VALUE).asText();
		
		return new FileValue(contentType, fileValue);
	}

}
