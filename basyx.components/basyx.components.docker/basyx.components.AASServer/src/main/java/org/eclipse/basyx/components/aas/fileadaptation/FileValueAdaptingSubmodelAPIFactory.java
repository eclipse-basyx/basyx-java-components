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


package org.eclipse.basyx.components.aas.fileadaptation;

import java.util.List;
import java.util.Optional;

import org.eclipse.basyx.aas.aggregator.AASAggregatorAPIHelper;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating a {@link FileValueAdaptingSubmodelAPI}
 * 
 * @author schnicke
 *
 */
public class FileValueAdaptingSubmodelAPIFactory implements ISubmodelAPIFactory {

	private static Logger logger = LoggerFactory.getLogger(FileValueAdaptingSubmodelAPIFactory.class);

	private String serverUrl;
	private ISubmodelAPIFactory submodelApiFactory;

	public FileValueAdaptingSubmodelAPIFactory(ISubmodelAPIFactory submodelApiFactory, String serverUrl) {
		this.serverUrl = serverUrl;
		this.submodelApiFactory = submodelApiFactory;
	}

	@Override
	public ISubmodelAPI getSubmodelAPI(Submodel submodel) {
		String aasId = getAasIdentifier(submodel);
		return new FileValueAdaptingSubmodelAPI(submodelApiFactory.create(submodel), getSubmodelUrl(serverUrl, aasId, submodel.getIdShort()));
	}

	private String getAasIdentifier(Submodel submodel) {
		IReference reference = submodel.getParent();

		if (reference != null && reference.getKeys() != null && reference.getKeys().size() > 0) {
			List<IKey> keys = reference.getKeys();
			Optional<IKey> aasKey = keys.stream().filter(k -> k.getType().equals(KeyElements.ASSETADMINISTRATIONSHELL)).findAny();
			if (aasKey.isPresent()) {
				return aasKey.get().getValue();
			}
		}
		
		logger.error("Submodel with id " + submodel.getIdentification() + " does not have a parent. The automatic setting of FileValues may lead to unexpected results.");

		return "";
	}

	private static String getSubmodelUrl(String serverUrl, String aasId, String submodelIdShort) {
		String aasAccessPath = AASAggregatorAPIHelper.getAASAccessPath(new CustomId(aasId));
		return VABPathTools.concatenatePaths(serverUrl, aasAccessPath, AssetAdministrationShell.SUBMODELS, submodelIdShort, SubmodelProvider.SUBMODEL);
	}

}
