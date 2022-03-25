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
package org.eclipse.basyx.components.devicemanager;

import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.ModelUrn;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.components.service.BaseBaSyxService;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/**
 * Base class for device managers
 * 
 * Device managers assume HTTP connection to BaSys infrastructure
 * 
 * Device managers manage devices that are not BaSys conforming by themselves.
 * They: - Register devices with the registry - Receive data from devices and
 * update sub models if necessary - Receive change requests from sub models and
 * update devices
 * 
 * @author kuhn
 *
 */
public abstract class DeviceManagerComponent extends BaseBaSyxService {

	/**
	 * Store VAB object ID of default AAS server
	 */
	protected String aasServerObjID = null;

	/**
	 * Store HTTP URL of AAS server
	 */
	protected String aasServerURL = null;

	/**
	 * Set AAS server VAB object ID
	 */
	protected void setAASServerObjectID(String objID) {
		aasServerObjID = objID;
	}

	/**
	 * Get AAS server VAB object ID
	 */
	protected String getAASServerObjectID() {
		return aasServerObjID;
	}

	/**
	 * Set AAS server URL
	 */
	protected void setAASServerURL(String srvUrl) {
		aasServerURL = srvUrl;
	}

	/**
	 * Get AAS server URL
	 */
	protected String getAASServerURL() {
		return aasServerURL;
	}

	/**
	 * Get AAS descriptor for managed device
	 */
	protected abstract AASDescriptor getAASDescriptor();

	/**
	 * Returns the actual endpoint of the AAS managed by this component
	 */
	protected String getAASEndpoint(ModelUrn aasURN) {
		return VABPathTools.concatenatePaths(getAASServerURL(), AASAggregatorProvider.PREFIX, aasURN.getEncodedURN(), "/aas");
	}

	/**
	 * Add sub model descriptor to AAS descriptor
	 * 
	 * @param aasDescriptor
	 *            AAS descriptor of AAS that sub model belongs to
	 * @param subModelURN
	 *            URN of sub model that will be described by descriptor
	 * 
	 * @return Sub model descriptor endpoint points to default AAS server location
	 *         and contains default prefix path
	 */
	protected SubmodelDescriptor addSubmodelDescriptorURI(AASDescriptor aasDescriptor, ModelUrn subModelURN, String subModelId) {
		// Create sub model descriptor
		String submodelEndpoint = VABPathTools.concatenatePaths(getAASServerURL(), AASAggregatorProvider.PREFIX, VABPathTools.encodePathElement(aasDescriptor.getIdentifier().getId()), "/aas/submodels", subModelId);
		SubmodelDescriptor submodelDescriptor = new SubmodelDescriptor(subModelId, subModelURN, submodelEndpoint);

		// Add sub model descriptor to AAS descriptor
		aasDescriptor.addSubmodelDescriptor(submodelDescriptor);

		// Return sub model descriptor
		return submodelDescriptor;
	}
}
