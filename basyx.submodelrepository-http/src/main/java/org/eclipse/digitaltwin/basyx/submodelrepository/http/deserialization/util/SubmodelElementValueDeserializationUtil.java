package org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;
import org.eclipse.digitaltwin.basyx.submodelservice.value.FileValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.MultiLanguagePropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.PropertyValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.RangeValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;

import com.fasterxml.jackson.databind.JsonNode;

public class SubmodelElementValueDeserializationUtil {
	
	private static final String CONTENT_TYPE = "contentType";
	private static final String MAX = "max";
	private static final String MIN = "min";
	private static final String VALUE = "value";

	private SubmodelElementValueDeserializationUtil() {
	    throw new IllegalStateException("Utility class");
	  }

	public static SubmodelElementValue createMultiLanguagePropertyValue(JsonNode node) {
		List<LangString> values = createLangStrings(node);
		
        return new MultiLanguagePropertyValue(values);
	}
	
	public static boolean isTypeOfPropertyValue(JsonNode node) {
		return node.size() == PropertyValue.class.getDeclaredFields().length && node.has(VALUE);
	}

	public static boolean isTypeOfFileValue(JsonNode node) {
		return node.size() == FileValue.class.getDeclaredFields().length && node.has(VALUE) && node.has(CONTENT_TYPE);
	}

	public static boolean isTypeOfRangeValue(JsonNode node) {
		return node.size() == RangeValue.class.getDeclaredFields().length && node.has(MIN) && node.has(MAX);
	}
	
	private static List<LangString> createLangStrings(JsonNode node) {
		List<LangString> langStrings = new ArrayList<>();
		
        for (JsonNode arrayNode : node) {
            Iterator<String> fieldNames = arrayNode.fieldNames();
            String language = fieldNames.next();
            String text = arrayNode.get(language).asText();
            langStrings.add(new DefaultLangString(text, language));
        }
        
		return langStrings;
	}

}
