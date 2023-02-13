/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization;

import java.io.IOException;
import org.eclipse.digitaltwin.basyx.submodelrepository.http.deserialization.factory.SubmodelElementValueFactory;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValueType;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Handles the mapping between a passed Submodel payload and AAS4J
 * 
 * @author schnicke
 *
 */
public class SubmodelElementValueJsonDeserializer extends JsonDeserializer<SubmodelElementValue> {

	@Override
	public SubmodelElementValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		try {
			ObjectMapper mapper = (ObjectMapper) p.getCodec();
			JsonNode node = mapper.readTree(p);
			if (!node.has("valueType")) {
				throw new IllegalArgumentException("Unable to determine concrete implementation based on JSON payload");
			}

			SubmodelElementValueType type = SubmodelElementValueType.fromString(node.get("valueType").asText());
			
			SubmodelElementValueFactory submodelElementValueFactory = new SubmodelElementValueFactory();
			
			return submodelElementValueFactory.create(type, node);

//			switch (type) {
//			case RANGE:
//				int min = node.get("min").asInt();
//				int max = node.get("max").asInt();
//				return new RangeValue(min, max);
//			case MULTI_LANGUAGE_PROPERTY_VALUE:
//				return mapper.convertValue(node, MultiLanguagePropertyValue.class);
//			case PROPERTY:
//				String value = node.get("value").asText();
//				return new PropertyValue(value);
//			case FILE:
//				String contentType = node.get("contentType").asText();
//				String fileValue = node.get("value").asText();
//				return new FileValue(contentType, fileValue);
//			default:
//				throw new IllegalArgumentException("Unsupported type: " + type);
//			}

//	        if (node.has("min") && node.has("max")) {
//	            int min = node.get("min").asInt();
//	            int max = node.get("max").asInt();
//	            return new RangeValue(min, max);
//	        } else if (node.isArray()) {
//	            List<LangString> value = mapper.readValue(node.toString(), mapper.getTypeFactory().constructCollectionType(List.class, LangString.class));
//	            return new MultiLanguagePropertyValue(value);
//	        } else if (node.has("value") && node.has("contentType")) {
//	        	String contentType = node.get("contentType").asText();
//                String value = node.get("value").asText();
//                return new FileValue(contentType, value);
//	        } else if(node.has("value")) {
//	        	String value = node.get("value").asText();
//                return new PropertyValue(value);
//	        }

//			throw new IllegalArgumentException("Unable to determine concrete implementation based on JSON payload");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}