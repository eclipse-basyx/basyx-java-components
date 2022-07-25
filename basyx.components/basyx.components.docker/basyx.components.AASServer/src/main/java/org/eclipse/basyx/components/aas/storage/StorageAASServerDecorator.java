package org.eclipse.basyx.components.aas.storage;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregatorFactory;
import org.eclipse.basyx.aas.restapi.api.IAASAPIFactory;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.extensions.submodel.storage.aggregator.StorageDecoratingSubmodelAggregatorFactory;
import org.eclipse.basyx.extensions.submodel.storage.api.StorageDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregatorFactory;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;

import jakarta.persistence.EntityManager;

/**
 *
 * Decorator for the storage option of the submodel
 *
 * @author fischer
 *
 */
public class StorageAASServerDecorator implements IAASServerDecorator {
	private String submodelElementStorageOption;
	private EntityManager entityManager;

	public StorageAASServerDecorator(EntityManager entityManager, String submodelElementStorageOption) {
		this.entityManager = entityManager;
		this.submodelElementStorageOption = submodelElementStorageOption;
	}

	@Override
	public ISubmodelAPIFactory decorateSubmodelAPIFactory(ISubmodelAPIFactory submodelAPIFactory) {
		return new StorageDecoratingSubmodelAPIFactory(submodelAPIFactory, entityManager, submodelElementStorageOption);
	}

	@Override
	public ISubmodelAggregatorFactory decorateSubmodelAggregatorFactory(ISubmodelAggregatorFactory submodelAggregatorFactory) {
		return new StorageDecoratingSubmodelAggregatorFactory(submodelAggregatorFactory, entityManager, submodelElementStorageOption);
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
