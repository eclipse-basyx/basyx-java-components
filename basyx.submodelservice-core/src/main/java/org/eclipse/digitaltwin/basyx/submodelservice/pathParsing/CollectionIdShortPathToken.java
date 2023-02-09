package org.eclipse.digitaltwin.basyx.submodelservice.pathParsing;

import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElement;
import org.eclipse.digitaltwin.aas4j.v3.model.SubmodelElementCollection;
import org.eclipse.digitaltwin.basyx.core.exceptions.ElementDoesNotExistException;

/**
 * Implementation of {@link PathToken} for SubmodelElementCollection idShort
 * tokens
 * 
 * @author fried
 *
 */
public class CollectionIdShortPathToken implements PathToken {

	private final String token;

	public CollectionIdShortPathToken(String token) {
		this.token = token;
	}

	@Override
	public SubmodelElement getSubmodelElement(SubmodelElement rootElement) {
		SubmodelElementCollection smc;
		try {
			smc = (SubmodelElementCollection) rootElement;
		} catch (Exception e) {
			throw new ElementDoesNotExistException();
		}
		return smc.getValue().stream().filter(sme -> sme.getIdShort().equals(token)).findAny()
				.orElseThrow(() -> new ElementDoesNotExistException(token));
	}

	@Override
	public String getToken() {
		return token;
	}

}
