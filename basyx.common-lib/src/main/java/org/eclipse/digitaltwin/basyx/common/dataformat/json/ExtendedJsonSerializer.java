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

import java.util.Collection;
import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.dataformat.SerializationException;
import org.eclipse.digitaltwin.aas4j.v3.dataformat.json.JsonSerializer;
import org.eclipse.digitaltwin.aas4j.v3.model.AssetInformation;
import org.eclipse.digitaltwin.aas4j.v3.model.Reference;
import org.eclipse.digitaltwin.basyx.common.dataformat.json.serialization.AssetInformationSerializer;
import org.eclipse.digitaltwin.basyx.common.dataformat.json.serialization.ReferenceSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * Class for serializing an objects of the BaSyx infrastructure to JSON.
 * 
 * @author jungjan
 *
 */
public class ExtendedJsonSerializer extends JsonSerializer implements AssetInformationSerializer, ReferenceSerializer {

	@Override
	public String write(Reference reference) throws SerializationException {
        try {
            return mapper.writeValueAsString(reference);
        } catch (JsonProcessingException e) {
            throw new SerializationException("error serializing Reference", e);
        }
	}
	
	@Override
	public String writeReferences(Collection<? extends Reference> references) throws SerializationException {
        if (references == null || references.isEmpty()) {
            return null;
        }
        try {
            return mapper.writerFor(mapper.getTypeFactory().constructCollectionType(List.class, references.iterator().next().getClass()))
                    .writeValueAsString(references);
        } catch (JsonProcessingException e) {
            throw new SerializationException("error serializing list of references", e);
        }
	}

	@Override
	public String write(AssetInformation assetInformation) throws SerializationException {
        try {
            return mapper.writeValueAsString(assetInformation);
        } catch (JsonProcessingException e) {
            throw new SerializationException("error serializing AssetInformation", e);
        }
	}

	@Override
	public String writeAssetInformations(Collection<? extends AssetInformation> assetInformations)
			throws SerializationException {
        if (assetInformations == null || assetInformations.isEmpty()) {
            return null;
        }
        try {
            return mapper.writerFor(mapper.getTypeFactory().constructCollectionType(List.class, assetInformations.iterator().next().getClass()))
                    .writeValueAsString(assetInformations);
        } catch (JsonProcessingException e) {
            throw new SerializationException("error serializing list of AssetInformation", e);
        }
	}
}
