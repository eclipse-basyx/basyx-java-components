package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.storage.StorageAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.extensions.submodel.storage.elements.IStorageSubmodelElement;
import org.eclipse.basyx.extensions.submodel.storage.elements.StorageSubmodelElementOperations;
import org.eclipse.basyx.extensions.submodel.storage.retrieval.StorageSubmodelElementRetrievalAPI;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.vab.protocol.api.IConnectorFactory;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnectorFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Class to test if the configuration of the AASServer with storage works
 *
 * @author fischer
 *
 */
@RunWith(Parameterized.class)
public class TestAASServerWithStorageOption {
	protected IAASRegistry aasRegistry;
	protected ConnectedAssetAdministrationShellManager manager;
	private String storageBackend;
	private AASServerComponent component;

	protected IIdentifier shellIdentifier = new CustomId("shellId");
	protected IIdentifier submodelIdentifier = new CustomId("submodelId");
	protected String propertyIdShort = "propertyIdShort";

	private StorageSubmodelElementRetrievalAPI retrievalAPI;
	private EntityManager entityManager;

	@Parameterized.Parameters
	public static String[] storageOptions() {
		return new String[] { "storageElement_sql", "storageElement_nosql" };
	}

	public TestAASServerWithStorageOption(String storageOption) {
		this.storageBackend = storageOption;
	}

	@Before
	public void setUp() {
		aasRegistry = new InMemoryRegistry();

		IConnectorFactory connectorFactory = new HTTPConnectorFactory();
		manager = new ConnectedAssetAdministrationShellManager(aasRegistry, connectorFactory);

		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);

		component = new AASServerComponent(contextConfig);
		component.addAASServerFeature(new StorageAASServerFeature("COMPLETE", storageBackend));
		component.startComponent();

		EntityManagerFactory emf = Persistence.createEntityManagerFactory(storageBackend);
		entityManager = emf.createEntityManager();
		retrievalAPI = new StorageSubmodelElementRetrievalAPI(entityManager);
	}

	@Test
	public void testCreateSubmodelWithElement() {
		AssetAdministrationShell shell = createShell(shellIdentifier.getId(), shellIdentifier);
		manager.createAAS(shell, component.getURL());

		Submodel submodel = createSubmodel(submodelIdentifier.getId(), submodelIdentifier);
		Property property = createProperty(propertyIdShort, true);
		submodel.addSubmodelElement(property);
		manager.createSubmodel(shellIdentifier, submodel);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(submodelIdentifier.getId(), propertyIdShort);
		assertEquals(property.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(StorageSubmodelElementOperations.CREATE, storedElement.getOperation());
	}

	@Test
	public void testUpdateSubmodelAddNewElement() {
		AssetAdministrationShell shell = createShell(shellIdentifier.getId(), shellIdentifier);
		manager.createAAS(shell, component.getURL());

		Submodel submodel = createSubmodel(submodelIdentifier.getId(), submodelIdentifier);
		Property property = createProperty(propertyIdShort, "old");
		submodel.addSubmodelElement(property);
		manager.createSubmodel(shellIdentifier, submodel);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(submodelIdentifier.getId(), propertyIdShort);
		assertEquals(property.getValue().toString(), storedElement.getSerializedElementValue());
		assertEquals(StorageSubmodelElementOperations.CREATE, storedElement.getOperation());

		// current limitation in MultiSubmodelProvider does only support the creation of
		// elements, therefore no update operation is checked
		property.setValue("new");
		submodel.addSubmodelElement(property);
		manager.createSubmodel(shellIdentifier, submodel);

		IStorageSubmodelElement updatedElement = getSingleStorageElementWithIdShort(submodelIdentifier.getId(), propertyIdShort);
		assertEquals(property.getValue().toString(), updatedElement.getSerializedElementValue());
		assertEquals(StorageSubmodelElementOperations.CREATE, updatedElement.getOperation());
	}

	@Test
	public void testDeleteSubmodel() {
		AssetAdministrationShell shell = createShell(shellIdentifier.getId(), shellIdentifier);
		manager.createAAS(shell, component.getURL());

		Submodel submodel = createSubmodel(submodelIdentifier.getId(), submodelIdentifier);
		Property property = createProperty(propertyIdShort, "deleteProperty");
		submodel.addSubmodelElement(property);
		manager.createSubmodel(shellIdentifier, submodel);

		manager.deleteSubmodel(shellIdentifier, submodelIdentifier);

		IStorageSubmodelElement storedElement = getSingleStorageElementWithIdShort(submodelIdentifier.getId(), propertyIdShort);
		assertEquals(StorageSubmodelElementOperations.DELETE, storedElement.getOperation());
	}

	@After
	public void tearDownClass() {
		component.stopComponent();
		List<IStorageSubmodelElement> elements = retrievalAPI.getSubmodelElementHistoricValues(submodelIdentifier.getId());
		entityManager.getTransaction().begin();
		elements.forEach((n) -> entityManager.remove(n));
		entityManager.getTransaction().commit();
	}

	protected AssetAdministrationShell createShell(String idShort, IIdentifier identifier) {
		AssetAdministrationShell shell = new AssetAdministrationShell();
		shell.setIdentification(identifier);
		shell.setIdShort(idShort);
		return shell;
	}

	protected Submodel createSubmodel(String idShort, IIdentifier submodelIdentifier) {
		Submodel submodel = new Submodel();
		submodel.setIdentification(submodelIdentifier);
		submodel.setIdShort(idShort);
		return submodel;
	}

	protected Property createProperty(String idShort, Object value) {
		Property property = new Property(value);
		property.setIdShort(idShort);
		return property;
	}

	private IStorageSubmodelElement getSingleStorageElementWithIdShort(String submodelId, String elementIdShort) {
		List<IStorageSubmodelElement> elements = retrievalAPI.getSubmodelElementHistoricValues(submodelId, elementIdShort);
		if (elements.isEmpty()) {
			return null;
		}

		IStorageSubmodelElement latestStoredElement = elements.get(0);
		return latestStoredElement;
	}

}
