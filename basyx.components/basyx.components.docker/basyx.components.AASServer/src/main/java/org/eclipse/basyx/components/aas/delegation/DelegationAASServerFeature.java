package org.eclipse.basyx.components.aas.delegation;

import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;

public class DelegationAASServerFeature implements IAASServerFeature {

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public IAASServerDecorator getDecorator() {
		return new DelegationAASServerDecorator();
	}

}
