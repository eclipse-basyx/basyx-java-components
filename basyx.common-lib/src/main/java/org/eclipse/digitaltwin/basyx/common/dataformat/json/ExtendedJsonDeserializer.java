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


package org.eclipse.digitaltwin.basyx.common.dataformat.json;

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.DeserializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonDeserializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.common.dataformat.json.deserialization.AssetInformationDeserializer;
import org.eclipse.digitaltwin.basyx.common.dataformat.json.deserialization.ReferenceDeserializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 
 * Class for deserializing/parsing JSON documents within the BaSyx infrastructure.
 *
 * @author jungjan
 *
 */
public class ExtendedJsonDeserializer extends JsonDeserializer implements AssetInformationDeserializer, ReferenceDeserializer {

	@Override
	public <T extends Reference> T readReference(String reference, Class<T> outputClass) throws DeserializationException {
        try {
            return mapper.readValue(reference, outputClass);
        } catch (JsonProcessingException e) {
            throw new DeserializationException("error deserializing Reference", e);
        }
	}

	@Override
	public <T extends Reference> List<T> readReferences(String references, Class<T> outputClass)
			throws DeserializationException {
		try {
            return mapper.readValue(references, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            throw new DeserializationException("error deserializing list of References", e);
        }
	}

	@Override
	public <T extends AssetInformation> T readAssetInformation(String assetInformation, Class<T> outputClass)
			throws DeserializationException {
        try {
            return mapper.readValue(assetInformation, outputClass);
        } catch (JsonProcessingException e) {
            throw new DeserializationException("error deserializing AssetInformation", e);
        }
	}

	@Override
	public <T extends AssetInformation> List<T> readAssetInformations(String assetInformations, Class<T> outputClass)
			throws DeserializationException {
		try {
            return mapper.readValue(assetInformations, new TypeReference<List<T>>() {
            });
        } catch (JsonProcessingException e) {
            throw new DeserializationException("error deserializing list of AssetInformations", e);
        }
	}

}
