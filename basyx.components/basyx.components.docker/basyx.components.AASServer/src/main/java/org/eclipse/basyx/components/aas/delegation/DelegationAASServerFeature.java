package org.eclipse.basyx.components.aas.delegation;

import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;

public class DelegationAASServerFeature implements IAASServerFeature {

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IAASServerDecorator getDecorator() {
		return new DelegationAASServerDecorator();
	}

}
