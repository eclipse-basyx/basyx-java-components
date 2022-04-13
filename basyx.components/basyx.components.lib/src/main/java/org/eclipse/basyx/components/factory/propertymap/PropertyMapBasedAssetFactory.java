/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
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

package org.eclipse.basyx.components.factory.propertymap;

import java.util.Map;

import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;

/**
 * Factory enabling {@link Asset} creation from a property map
 * 
 * @author schnicke
 *
 */
public class PropertyMapBasedAssetFactory {

	/**
	 * Creates an {@link Asset} based on the data contained in the property map<br>
	 * <br>
	 * Example:
	 * 
	 * <pre>
	 * { 
	 * 		"id": "assetId", 
	 * 		"idShort": "assetIdShort"
	 * }
	 * </pre>
	 * 
	 * @param assetMap
	 * @return
	 */
	public Asset create(Map<String, String> assetMap) {
		String assetId = PropertyMapConstantsHelper.getIdValue(assetMap);
		String assetIdShort = PropertyMapConstantsHelper.getIdShort(assetMap);

		return new Asset(assetIdShort, new CustomId(assetId), AssetKind.INSTANCE);
	}

}
