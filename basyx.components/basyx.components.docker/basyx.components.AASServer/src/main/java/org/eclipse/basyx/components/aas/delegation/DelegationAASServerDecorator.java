package org.eclipse.basyx.components.aas.delegation;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.extensions.submodel.delegation.DelegatingDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

public class DelegationAASServerDecorator implements IAASServerDecorator{

	@Override
	public ISubmodelAPIFactory decorateSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new DelegatingDecoratingSubmodelAPIFactory(submodelAPIFactory);
	}

	@Override
	public ISubmodelAggregatorFactory decorateSubmodelAggregatorFactory(
			ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return submodelAggregatorFactory;
	}

	@Override
	public IAASAPIFactory decorateAASAPIFactory(IAASAPIFactory aasAPIFactory) {
		return aasAPIFactory;
	}

	@Override
	public IAASAggregatorFactory decorateAASAggregatorFactory(IAASAggregatorFactory aasAggregatorFactory) {
		return aasAggregatorFactory;
	}
}
