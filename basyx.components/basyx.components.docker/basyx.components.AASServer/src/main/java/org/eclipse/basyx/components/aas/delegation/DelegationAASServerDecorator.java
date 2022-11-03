package org.eclipse.basyx.components.aas.delegation;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

public class DelegationAASServerDecorator implements IAASServerDecorator{

	@Override
	public ISubmodelAPIFactory decorateSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		// TODO handle delegation here
		return null;
	}

	@Override
	public ISubmodelAggregatorFactory decorateSubmodelAggregatorFactory(
			ISubmodelAggregatorFactory submodelAggregatorFactory) {
		// TODO doesn't matter
		return null;
	}

	@Override
	public IAASAPIFactory decorateAASAPIFactory(IAASAPIFactory aasAPIFactory) {
		// TODO doesn't matter
		return null;
	}

	@Override
	public IAASAggregatorFactory decorateAASAggregatorFactory(IAASAggregatorFactory aasAggregatorFactory) {
		// TODO doesn't matter 
		return null;
	}

}
