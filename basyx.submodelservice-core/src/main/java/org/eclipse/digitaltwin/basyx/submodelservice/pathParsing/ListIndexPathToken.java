package org.eclipse.digitaltwin.basyx.submodelservice.pathParsing;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementList;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Implementation of {@link PathToken} for List Index Tokens
 * 
 * @author fried
 *
 */
public class ListIndexPathToken implements PathToken {

	private final String token;

	public ListIndexPathToken(String token) {
		this.token = token;
	}

	@Override
	public SubmodelElement getSubmodelElement(SubmodelElement rootElement) {
		SubmodelElementList sml;
		try {
			sml = (SubmodelElementList) rootElement;
		} catch (Exception e) {
			throw new ElementDoesNotExistException();
		}
		int index = getIndexFromToken(token);
		if (index > sml.getValue().size() - 1) {
			throw new ElementDoesNotExistException(rootElement.getIdShort() + token);
		}
		return sml.getValue().get(index);
	}

	private int getIndexFromToken(String token) {
		return Integer.valueOf(token);
	}

	@Override
	public String getToken() {
		return token;
	}

}
