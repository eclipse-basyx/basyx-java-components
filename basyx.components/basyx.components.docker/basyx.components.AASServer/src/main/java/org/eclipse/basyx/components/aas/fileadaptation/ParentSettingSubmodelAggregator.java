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

import java.util.Collection;

import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.reference.Key;
import org.eclipse.basyx.submodel.metamodel.map.reference.Reference;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SubmodelAggregator that adds missing parent references in Submodels
 * 
 * @author schnicke
 *
 */
public class ParentSettingSubmodelAggregator implements ISubmodelAggregator {
	private static Logger logger = LoggerFactory.getLogger(ParentSettingSubmodelAggregator.class);

	private ISubmodelAggregator submodelAggregator;
	private IIdentifier aasIdentifier;

	public ParentSettingSubmodelAggregator(ISubmodelAggregator submodelAggregator, IIdentifier aasIdentifier) {
		this.submodelAggregator = submodelAggregator;
		this.aasIdentifier = aasIdentifier;
	}

	@Override
	public Collection<ISubmodel> getSubmodelList() {
		return submodelAggregator.getSubmodelList();
	}

	@Override
	public ISubmodel getSubmodel(IIdentifier identifier) throws ResourceNotFoundException {
		return submodelAggregator.getSubmodel(identifier);
	}

	@Override
	public ISubmodel getSubmodelbyIdShort(String idShort) throws ResourceNotFoundException {
		return submodelAggregator.getSubmodelbyIdShort(idShort);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIById(IIdentifier identifier) throws ResourceNotFoundException {
		return submodelAggregator.getSubmodelAPIById(identifier);
	}

	@Override
	public ISubmodelAPI getSubmodelAPIByIdShort(String idShort) throws ResourceNotFoundException {
		return submodelAggregator.getSubmodelAPIByIdShort(idShort);
	}

	@Override
	public void createSubmodel(Submodel submodel) {
		if (!parentIsSet(submodel)) {
			addParentToSubmodel(aasIdentifier, submodel);
		}

		submodelAggregator.createSubmodel(submodel);
	}

	private boolean parentIsSet(Submodel submodel) {
		return submodel.getParent() != null && 
				submodel.getParent().getKeys() != null && 
				submodel.getParent().getKeys().size() > 0;
	}

	private static void addParentToSubmodel(IIdentifier aasIdentifier, Submodel submodel) {
		submodel.setParent(getAasReference(aasIdentifier));
		logger.info("Submodel with Identifier " + submodel.getIdentification() + " is missing parent. Setting it to " + submodel.getParent());
	}

	private static IReference getAasReference(IIdentifier aasIdentifier) {
		return new Reference(new Key(KeyElements.ASSETADMINISTRATIONSHELL, false, aasIdentifier.getId(), aasIdentifier.getIdType()));
	}

	@Override
	public void createSubmodel(ISubmodelAPI submodelAPI) {
		submodelAggregator.createSubmodel(submodelAPI);
	}

	@Override
	public void updateSubmodel(Submodel submodel) throws ResourceNotFoundException {
		submodelAggregator.updateSubmodel(submodel);
	}

	@Override
	public void deleteSubmodelByIdentifier(IIdentifier identifier) {
		submodelAggregator.deleteSubmodelByIdentifier(identifier);
	}

	@Override
	public void deleteSubmodelByIdShort(String idShort) {
		submodelAggregator.deleteSubmodelByIdShort(idShort);
	}
}
