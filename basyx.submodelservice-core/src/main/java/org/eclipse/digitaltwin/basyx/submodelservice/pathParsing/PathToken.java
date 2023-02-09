package org.eclipse.digitaltwin.basyx.submodelservice.pathParsing;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;

/**
 * Interface for idShortPath Tokens
 * 
 * @author fried
 *
 */
public interface PathToken {

	/**
	 * Retrieve the Nested SubmodelElement from the rootElement
	 * 
	 * @param rootElement the SubmodelElement, the nested SubmodelElment is in
	 * @return the nested SubmodelElement in the rootElement
	 */
	public SubmodelElement getSubmodelElement(SubmodelElement rootElement);

	/**
	 * Returns the token
	 * 
	 * @return the token
	 */
	public String getToken();
}
