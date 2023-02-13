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

package org.eclipse.digitaltwin.basyx.submodelservice;

import java.util.Collection;

import org.eclipse.digitaltwin.aas4j.v3.model.Property;
import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;
import org.eclipse.digitaltwin.basyx.submodelservice.pathParsing.HierarchicalSubmodelElementParser;
import org.eclipse.digitaltwin.basyx.submodelservice.value.SubmodelElementValue;
import org.eclipse.digitaltwin.basyx.submodelservice.value.factory.SubmodelElementValueFactory;


/**
 * Implements the SubmodelService as in-memory variant
 * 
 * @author schnicke, danish
 * 
 */
public class InMemorySubmodelService implements SubmodelService {

	private final Submodel submodel;
	private HierarchicalSubmodelElementParser parser;

	/**
	 * Creates the InMemory SubmodelService containing the passed Submodel
	 * 
	 * @param submodel
	 */
	public InMemorySubmodelService(Submodel submodel) {
		this.submodel = submodel;
		parser = new HierarchicalSubmodelElementParser(submodel);
	}

	@Override
	public Submodel getSubmodel() {
		return submodel;
	}

	@Override
	public Collection<SubmodelElement> getSubmodelElements() {
		return submodel.getSubmodelElements();
	}

	@Override
	public SubmodelElement getSubmodelElement(String idShortPath) throws ElementDoesNotExistException {
		return parser.getSubmodelElementFromIdShortPath(idShortPath);
	}

	@Override
	public SubmodelElementValue getSubmodelElementValue(String idShortPath) throws ElementDoesNotExistException {
		SubmodelElementValueFactory submodelElementValueFactory = new SubmodelElementValueFactory();
		
		return submodelElementValueFactory.create(getSubmodelElement(idShortPath));
	}

	@Override
	public void setSubmodelElementValue(String idShortPath, Object value) throws ElementDoesNotExistException {
		Property property = (Property) getSubmodelElement(idShortPath);
		property.setValue((String) value);
	}
}