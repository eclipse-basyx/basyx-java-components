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

import java.util.List;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Class for getting a Hierarchical SubmodelElement in a Submodel via a idShort
 * Path
 * 
 * @author fried
 *
 */
public class HierarchicalSubmodelElementParser {
	private String idShortPath;
	private Submodel submodel;

	/**
	 * Creates a HierarchicalSubmodelElementParser
	 * 
	 * @param submodel    the submodel
	 * @param idShortPath the idShortPath
	 */
	public HierarchicalSubmodelElementParser(Submodel submodel, String idShortPath) {
		this.idShortPath = idShortPath;
		this.submodel = submodel;
	}

	/**
	 * Returns the nested SubmodelElement given in the idShortPath
	 * 
	 * @return the nested SubmodelElement
	 */
	public SubmodelElement getSubmodelElement() {
		String[] splittedPath = SubmodelElementIdShortPathParser.parsePath(idShortPath);

		SubmodelElement submodelElement = getFirstSubmodelElement(
				SubmodelElementIdShortPathParser.getIdShortWithoutIndices(getFirstIdShort(splittedPath)));

		for (int i = 0; i < splittedPath.length; i++) {
			String idShort = splittedPath[i];
			SubmodelElement currentSubmodelElement = getSubmodelElementInSubmodelElement(submodelElement,
					SubmodelElementIdShortPathParser.getIdShortWithoutIndices(idShort));
			if (isSubmodelElementList(currentSubmodelElement)) {
				List<Integer> indices = SubmodelElementIdShortPathParser.getAllIndices(idShort);
				for (int index : indices) {
					currentSubmodelElement = getSubmodelElementFromSubmodelElementList(currentSubmodelElement, index);
				}
			}
			submodelElement = currentSubmodelElement;
		}

		return submodelElement;
	}

	private boolean isSubmodelElementList(SubmodelElement currentSubmodelElement) {
		return currentSubmodelElement instanceof SubmodelElementList;
	}

	private String getFirstIdShort(String[] splittedPath) {
		return splittedPath[0];
	}

	private SubmodelElement getSubmodelElementFromSubmodelElementList(SubmodelElement submodelElement, int index) {
		try {
			SubmodelElementList sml = (SubmodelElementList) submodelElement;
			return sml.getValue().get(index);
		} catch (Exception e) {
			throw new ElementDoesNotExistException(submodelElement.getIdShort() + "[" + index + "]");
		}
	}

	private SubmodelElement getSubmodelElementInSubmodelElement(SubmodelElement submodelElement, String nextIdShort) {
		if (submodelElement.getIdShort().equals(nextIdShort)) {
			return submodelElement;
		}
		if (isSubmodelElementList(submodelElement)) {
			SubmodelElementList sml = (SubmodelElementList) submodelElement;
			return sml.getValue().stream().filter(sme -> sme.getIdShort().equals(nextIdShort)).findAny()
					.orElseThrow(() -> new ElementDoesNotExistException(nextIdShort));
		} else if (submodelElement instanceof SubmodelElementCollection) {
			SubmodelElementCollection smc = (SubmodelElementCollection) submodelElement;
			return smc.getValue().stream().filter(sme -> sme.getIdShort().equals(nextIdShort)).findAny()
					.orElseThrow(() -> new ElementDoesNotExistException(nextIdShort));
		}
		return null;
	}

	private SubmodelElement getFirstSubmodelElement(String idShort) {
		return submodel.getSubmodelElements().stream().filter(sme -> sme.getIdShort().equals(idShort)).findAny()
				.orElseThrow(() -> new ElementDoesNotExistException(idShort));
	}
}
