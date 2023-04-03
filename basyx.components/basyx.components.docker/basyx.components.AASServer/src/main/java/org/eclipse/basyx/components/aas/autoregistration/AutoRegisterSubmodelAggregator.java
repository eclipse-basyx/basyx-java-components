package org.eclipse.basyx.components.aas.autoregistration;

import java.util.Collection;

import org.eclipse.basyx.aas.aggregator.AASAggregatorAPIHelper;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.restapi.MultiSubmodelProvider;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.restapi.SubmodelProvider;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.eclipse.basyx.vab.modelprovider.VABPathTools;

/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

/**
 * 
 * Implementation of a {@link ISubmodelAggregator} to automatically register
 * Submodels in the registry
 * 
 * @author fried
 *
 */
public class AutoRegisterSubmodelAggregator implements ISubmodelAggregator {

	private IIdentifier aasIdentifier;
	private ISubmodelAggregator aggregator;
	private IAASRegistry registry;
	private String endpoint;

	public AutoRegisterSubmodelAggregator(ISubmodelAggregator aggregator, IAASRegistry registry,
			IIdentifier aasIdentifier, String endpoint) {
		this.aggregator = aggregator;
		this.registry = registry;
		this.aasIdentifier = aasIdentifier;
		this.endpoint = endpoint;
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		return aggregator.getSubmodelList();
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException {
		return aggregator.getSubmodel(identifier);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException {
		return aggregator.getSubmodelbyIdShort(idShort);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException {
		return aggregator.getSubmodelAPIById(identifier);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException {
		return aggregator.getSubmodelAPIByIdShort(idShort);
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		aggregator.createSubmodel(submodel);
		registry.register(aasIdentifier, new SubmodelDescriptor(submodel, getEndpoint(submodel)));
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		aggregator.createSubmodel(submodelAPI);
		registry.register(aasIdentifier, new SubmodelDescriptor(submodelAPI.getSubmodel(), getEndpoint(submodelAPI.getSubmodel())));

	}

	private String getEndpoint(ISubmodel submodel) {
		String harmonized = AASAggregatorAPIHelper.harmonizeURL(endpoint);
		String shellEntryPath = AASAggregatorAPIHelper.getAASEntryPath(aasIdentifier);
		String submodelEntryPath = VABPathTools.concatenatePaths(MultiSubmodelProvider.SUBMODELS_PREFIX, submodel.getIdShort(), SubmodelProvider.SUBMODEL);
		return VABPathTools.concatenatePaths(harmonized, shellEntryPath, submodelEntryPath);
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		aggregator.updateSubmodel(submodel);
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		aggregator.deleteSubmodelByIdentifier(identifier);
		registry.delete(aasIdentifier, identifier);
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		Submodel submodel = (Submodel) aggregator.getSubmodelbyIdShort(idShort);
		aggregator.deleteSubmodelByIdShort(idShort);
		registry.delete(aasIdentifier, submodel.getIdentification());
	}

}
