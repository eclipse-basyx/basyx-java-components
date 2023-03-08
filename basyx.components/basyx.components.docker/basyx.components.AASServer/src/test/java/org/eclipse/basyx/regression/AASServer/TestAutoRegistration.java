package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.api.parts.asset.AssetKind;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.metamodel.map.parts.Asset;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.autoregistration.AutoRegisterAASAggregator;
import org.eclipse.basyx.components.aas.autoregistration.AutoRegisterSubmodelAggregator;
import org.eclipse.basyx.components.aas.configuration.AASServerBackend;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.junit.After;
import org.junit.Before;
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
	private AssetAdministrationShell dummyShell = createDummyShell();
	private Submodel dummySubmodel = createDummySubmodel();
	private IAASAggregator aasAggregator;
	private AASServerComponent aasServerComponent;
	private RegistryComponent registryComponent;
	private IAASRegistry registry;

	@Before
	public void setUp() {
		String registryComponentUrl = createRegistryComponent();

		String aggregatorComponentUrl = createAASServerComponent(registryComponentUrl);

		aasAggregator = new AASAggregatorProxy(aggregatorComponentUrl);
		registry = new AASRegistryProxy(registryComponentUrl);
	}

	private String createAASServerComponent(String registryUrl) {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		BaSyxAASServerConfiguration aasConfig = new BaSyxAASServerConfiguration();
		aasConfig.setAASBackend(AASServerBackend.INMEMORY);
		aasConfig.setRegistry(registryUrl);

		aasServerComponent = new AASServerComponent(contextConfig, aasConfig);
		aasServerComponent.startComponent();

		return contextConfig.getUrl();
	}

	private String createRegistryComponent() {
		BaSyxContextConfiguration registryContextConfig = new BaSyxContextConfiguration();
		registryContextConfig.setPort(4001);
		registryContextConfig.setContextPath("/testRegistry");

		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration();

		registryComponent = new RegistryComponent(registryContextConfig, registryConfig);
		registryComponent.startComponent();

		return registryContextConfig.getUrl();
	}

	@Test
	public void createdShellIsRegistered() {
		aasAggregator.createAAS(dummyShell);

		AASDescriptor shellDescriptor = registry.lookupAAS(dummyShell.getIdentification());

		assertEquals(dummyShell.getIdentification(), shellDescriptor.getIdentifier());
		
		assertShellIsAccessible(dummyShell.getIdentification());
	}

	private void assertShellIsAccessible(IIdentifier identification) {
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);
		manager.retrieveAAS(identification);
	}

	@Test
	public void deletedShellIsUnregistered() {
		aasAggregator.createAAS(dummyShell);
		aasAggregator.deleteAAS(dummyShell.getIdentification());
		
		try {
			registry.lookupAAS(dummyShell.getIdentification());
			fail();
		} catch (ResourceNotFoundException expected) {
		}
	}

	@Test
	public void createdSubmodelIsRegistered() {
		aasAggregator.createAAS(dummyShell);

		ConnectedAssetAdministrationShell connectedShell = (ConnectedAssetAdministrationShell) aasAggregator.getAAS(dummyShell.getIdentification());
		connectedShell.addSubmodel(dummySubmodel);

		SubmodelDescriptor submodelDescriptor = registry.lookupSubmodel(dummyShell.getIdentification(), dummySubmodel.getIdentification());
		assertEquals(dummySubmodel.getIdentification(), submodelDescriptor.getIdentifier());

		assertSubmodelIsAccessible(dummyShell.getIdentification(), dummySubmodel.getIdentification());
	}

	private void assertSubmodelIsAccessible(IIdentifier shellIdentification, IIdentifier submodelIdentification) {
		ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);
		manager.retrieveSubmodel(shellIdentification, submodelIdentification);
	}

	@Test
	public void createdSubmodelIsUnregistered() {
		aasAggregator.createAAS(dummyShell);
		ConnectedAssetAdministrationShell connectedShell = (ConnectedAssetAdministrationShell) aasAggregator.getAAS(dummyShell.getIdentification());
		connectedShell.addSubmodel(dummySubmodel);
		connectedShell.removeSubmodel(dummySubmodel.getIdentification());

		try {
			registry.lookupSubmodel(dummyShell.getIdentification(), dummySubmodel.getIdentification());
			fail();
		} catch (ResourceNotFoundException expected) {
		}
	}

	private AssetAdministrationShell createDummyShell() {
		Asset asset = new Asset("assetIdShort", new Identifier(IdentifierType.CUSTOM, "assetIdentifier"), AssetKind.INSTANCE);
		IIdentifier identifier = new Identifier(IdentifierType.CUSTOM, "dummyShellIdentifier");
		return new AssetAdministrationShell("dummyShellIdShort", identifier, asset);
	}

	private Submodel createDummySubmodel() {
		return new Submodel("dummySubmodelId", new Identifier(IdentifierType.CUSTOM,
				"dummySubmodelIdentifier"));
	}

	@After
	public void tearDown() {
		aasServerComponent.stopComponent();
		registryComponent.stopComponent();
	}

}
