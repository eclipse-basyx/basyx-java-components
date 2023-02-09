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

import java.util.Stack;

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
	private Submodel submodel;

	/**
	 * Creates a HierarchicalSubmodelElementParser
	 * 
	 * @param submodel the submodel
	 * 
	 */
	public HierarchicalSubmodelElementParser(Submodel submodel) {
		this.submodel = submodel;
	}

	/**
	 * Returns the nested SubmodelElement given in the idShortPath
	 * 
	 * @param idShortPath the isShortPath of the SubmodelElement
	 * 
	 * @return the nested SubmodelElement
	 * @throws ElementDoesNotExistException
	 * 
	 */
	public SubmodelElement getSubmodelElementFromIdShortPath(String idShortPath) throws ElementDoesNotExistException {
		SubmodelElementIdShortPathParser pathParser = new SubmodelElementIdShortPathParser();
		Stack<String> idShortPathTokenStack = pathParser.parsePathTokens(idShortPath);

		return getLastElementOfStack(idShortPathTokenStack);
	}

	private SubmodelElement getLastElementOfStack(Stack<String> idShortPathTokenStack) {
		String rootElementIdShort = idShortPathTokenStack.pop();
		SubmodelElement rootElement = getFirstSubmodelElementFromStack(rootElementIdShort);
		while (!idShortPathTokenStack.isEmpty()) {
			String token = idShortPathTokenStack.pop();
			rootElement = getNextSubmodelElement(rootElement, token);
		}

		return rootElement;
	}

	private SubmodelElement getFirstSubmodelElementFromStack(String rootElementIdShort) {
		return submodel.getSubmodelElements().stream().filter(sme -> sme.getIdShort().equals(rootElementIdShort))
				.findAny().orElseThrow(() -> new ElementDoesNotExistException());
	}

	private SubmodelElement getNextSubmodelElement(SubmodelElement element, String token) {
		if (isIndex(token)) {
			SubmodelElementList sml = (SubmodelElementList) element;
			int index = getIndexFromToken(token);
			if (index > sml.getValue().size() - 1) {
				throw new ElementDoesNotExistException(element.getIdShort() + token);
			}
			return sml.getValue().get(index);
		}
		SubmodelElementCollection smc = (SubmodelElementCollection) element;

		return smc.getValue().stream().filter(sme -> sme.getIdShort().equals(token)).findAny()
				.orElseThrow(() -> new ElementDoesNotExistException(token));
	}

	private int getIndexFromToken(String token) {
		int start = token.indexOf('[');
		int end = token.indexOf(']');
		return Integer.valueOf(token.substring(start + 1, end));
	}

	private boolean isIndex(String token) {
		return token.contains("[");
	}

}
