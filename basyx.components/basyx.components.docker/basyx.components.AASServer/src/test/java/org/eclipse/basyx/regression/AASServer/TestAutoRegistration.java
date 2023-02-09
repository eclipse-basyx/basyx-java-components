package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;

import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.components.aas.autoregistration.AutoRegisterAASAggregator;
import org.eclipse.basyx.components.aas.autoregistration.AutoRegisterSubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.SubmodelAggregator;
import org.eclipse.basyx.submodel.aggregator.api.ISubmodelAggregator;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.Test;

/**
 * 
 * Class to test the automatic registration via the
 * {@link AutoRegisterAASAggregator} and {@link AutoRegisterSubmodelAggregator}
 * 
 * @author fried
 *
 */
public class TestAutoRegistration {

	private static final String TEST_ENDPOINT = "testEndpoint";
	private static final String DUMMY_SHELL_ID_SHORT = "dummyShellIdShort";
	private static final Identifier DUMMY_SHELL_IDENTIFIER = new Identifier(IdentifierType.CUSTOM,
			"dummyShellIdentifier");
	private static final Asset DUMMY_SHELL_ASSET = new Asset("assetIdShort",
			new Identifier(IdentifierType.CUSTOM, "assetIdentifier"), AssetKind.INSTANCE);

	private static final String DUMMY_SUBMODEL_ID_SHORT = "dummySubmodelId";
	private static final Identifier DUMMY_SUBMODEL_IDENTIFIER = new Identifier(IdentifierType.CUSTOM,
			"dummySubmodelIdentifier");

	private IAASRegistry registry = new InMemoryRegistry();
	private AssetAdministrationShell dummyShell = createDummyShell();
	private Submodel dummySubmodel = createDummySubmodel();
	private IAASAggregator aasAggregator;
	private ISubmodelAggregator smAggregator;

	@Test
	public void createdShellIsRegisteredAutomatically() {
		createShellAndAggregator();
		assertEquals(dummyShell.getIdentification(), registry.lookupAAS(DUMMY_SHELL_IDENTIFIER).getIdentifier());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void deletedShellIsUnregisteredAutomatically() {
		createShellAndAggregator();
		aasAggregator.deleteAAS(DUMMY_SHELL_IDENTIFIER);
		registry.lookupAAS(DUMMY_SHELL_IDENTIFIER);
	}

	@Test
	public void createdSubmodelIsRegisteredAutomatically() {
		createShellAndAggregator();
		createSubmodelAndAggregator();

		IIdentifier foundSubmodelIdentifier = lookupSubmodelInRegistry();

		assertEquals(DUMMY_SUBMODEL_IDENTIFIER, foundSubmodelIdentifier);
	}

	@Test(expected = ResourceNotFoundException.class)
	public void createdSubmodelIsUnregisteredAutomatically() {
		createShellAndAggregator();
		createSubmodelAndAggregator();

		smAggregator.deleteSubmodelByIdShort(DUMMY_SUBMODEL_ID_SHORT);
		lookupSubmodelInRegistry();
	}

	private void createShellAndAggregator() {
		aasAggregator = new AutoRegisterAASAggregator(new AASAggregator(), registry, TEST_ENDPOINT);
		aasAggregator.createAAS(dummyShell);
	}

	private IIdentifier lookupSubmodelInRegistry() {
		return registry.lookupSubmodel(DUMMY_SHELL_IDENTIFIER, DUMMY_SUBMODEL_IDENTIFIER).getIdentifier();
	}

	private void createSubmodelAndAggregator() {
		smAggregator = new AutoRegisterSubmodelAggregator(new SubmodelAggregator(), registry, DUMMY_SHELL_IDENTIFIER,
				TEST_ENDPOINT);
		smAggregator.createSubmodel(dummySubmodel);
	}

	private AssetAdministrationShell createDummyShell() {
		return new AssetAdministrationShell(DUMMY_SHELL_ID_SHORT, DUMMY_SHELL_IDENTIFIER, DUMMY_SHELL_ASSET);
	}

	private Submodel createDummySubmodel() {
		return new Submodel(DUMMY_SUBMODEL_ID_SHORT, DUMMY_SUBMODEL_IDENTIFIER);
	}

}
