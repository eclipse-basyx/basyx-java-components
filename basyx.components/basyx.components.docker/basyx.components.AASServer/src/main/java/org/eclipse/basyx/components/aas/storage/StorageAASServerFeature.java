package org.eclipse.basyx.components.aas.storage;

import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * Feature for the storage option of the submodel
 *
 * @author fischer
 *
 */
public class StorageAASServerFeature implements IAASServerFeature {
	private String submodelElementStorageOption;
	private String submodelElementStorageBackend;
	private EntityManager entityManager;

	public StorageAASServerFeature(String submodelElementStorageOption, String submodelElementStorageBackend) {
		this.submodelElementStorageOption = submodelElementStorageOption;
		this.submodelElementStorageBackend = submodelElementStorageBackend;
	}

	@Override
	public void initialize() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory(submodelElementStorageBackend);
		entityManager = emf.createEntityManager();
	}

	@Override
	public void cleanUp() {
		entityManager.close();
	}

	@Override
	public IAASServerDecorator getDecorator() {
		return new StorageAASServerDecorator(entityManager, submodelElementStorageOption);
	}

}
