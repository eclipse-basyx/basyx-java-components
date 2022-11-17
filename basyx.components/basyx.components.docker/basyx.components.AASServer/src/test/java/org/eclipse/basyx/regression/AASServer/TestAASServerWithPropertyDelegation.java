package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.janino.util.DeepCopier;
import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.IComponent;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.delegation.DelegationAASServerDecorator;
import org.eclipse.basyx.components.aas.delegation.DelegationAASServerFeature;
import org.eclipse.basyx.components.aas.mongodb.MongoDBAASAggregator;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.components.registry.configuration.RegistryBackend;
import org.eclipse.basyx.extensions.aas.registration.authorization.AuthorizedAASRegistryProxy;
import org.eclipse.basyx.extensions.shared.delegation.PropertyDelegationManager;
import org.eclipse.basyx.extensions.submodel.delegation.DelegatingDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifier;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.modelprovider.api.IModelProvider;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAASServerWithPropertyDelegation {
	private Logger logger = LoggerFactory.getLogger(TestAASServerWithPropertyDelegation.class);
	
	private static ISubmodelAPI submodelAPI;
	ISubmodelAPIFactory submodelAPIFactory;
	private static AASServerComponent aasServerComponent;
	private static String REGISTRYPATH = "http://localhost:4000/registry";
	private static IAASRegistry registry;
	private static IComponent registryComponent;
	
	private static final int EXPECTED_VALUE = 10;
	private static final String SERVER_URL = "https://reqres.in";
	private static final String ENDPOINT = "/api/users/2";
	
	private static IAASAggregator aggregator;
	
	private static IIdentifier aasIdentifier = new CustomId("testAAS");
	private static IIdentifier smIdentifier = new CustomId("testSM");
	private static AssetAdministrationShell aas;
	private static Submodel submodel;
	private static AASAggregatorProvider provider;
	
	@BeforeClass
	public static void init() {
		aggregator = new AASAggregator();
		
		submodel = new Submodel("testSubmodel", smIdentifier);
		
		Property delegatedProperty = createDelegatedProperty();
		
		submodel.addSubmodelElement(delegatedProperty);
		
		aas = new AssetAdministrationShell();
		aas.setIdentification(aasIdentifier);
		aas.setIdShort("test");
		aas.addSubmodel(submodel);
		
		startRegistryServer();
		
		BaSyxAASServerConfiguration aasContextConfig = configureAASServer();
		
		startAASServerComponent(configureBasyxContext("aasContext.properties"), aasContextConfig);
		
//		DelegatingDecoratingSubmodelAPIFactory delegationDecoratingSubmodelAPIFactory = new DelegatingDecoratingSubmodelAPIFactory(new VABSubmodelAPIFactory());
//		
//		submodelAPI = delegationDecoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
		
//		registry = createAASRegistryProxy(aasContextConfig);
		provider = new AASAggregatorProvider(aggregator);
		
		aggregator.createAAS(aas);
		pushSubmodel(submodel, aas.getIdentification());
	}
	
	private static void pushSubmodel(Submodel sm, IIdentifier aasIdentifier) {
		provider.setValue("/" + AASAggregatorProvider.PREFIX + "/" + aasIdentifier.getId() + "/aas/submodels/" + sm.getIdShort(), sm);
	}
	
	@Test
	public void currentValueFromDelegatedEndpoint() {
		ISubmodel submodel = getSubmodelFromAggregator(aggregator);
		System.out.println("Submodel : " + submodel.toString());
//		logger.info("Submodel : " + submodel.toString());
//		ISubmodelElement smc = (ISubmodelElement) submodelAPI.getSubmodel().getSubmodelElement("test");
//		logger.info("Current Value SMC : {} ", smc);
//		logger.info("Current Value : {} ", smc.getValue());
//		
//		assertEquals("abc", smc.getValue());
		
//		ISubmodel assetAdministrationShell = aggregator.getAAS(aasIdentifier).getSubmodel(smIdentifier);
//		ConnectedAssetAdministrationShell connectedAAS = (ConnectedAssetAdministrationShell) aggregator.getAAS(aasIdentifier);
//		logger.info("Submodel : {}", connectedAAS.getSubmodel(smIdentifier));
//		aasServerComponent.
//		List<AASDescriptor> aasDescriptors = registry.lookupAll();
////		
//		aasDescriptors.stream().forEach(aas -> System.out.println("aas : " + aas.toString()));
		System.out.println("Prop Val : " + getPropertyValueFromSubmodel(submodel));
	}
	
	private String getPropertyValueFromSubmodel(ISubmodel submodel2) {
		IModelProvider aasProvider = aggregator.getAASProvider(aasIdentifier);
//		Object smObject = aasProvider.getValue("/aas/submodels/testSubmodel/submodel/submodelElements/delegated");
		Object propValue = aasProvider.getValue("/aas/submodels/testSubmodel/submodel/submodelElements/delegated");
//		ISubmodel persistentSM = Submodel.createAsFacade((Map<String, Object>) smObject);
		return propValue.toString();
	}

	@SuppressWarnings("unchecked")
	private ISubmodel getSubmodelFromAggregator(IAASAggregator aggregator) {
		IModelProvider aasProvider = aggregator.getAASProvider(aasIdentifier);
		Object smObject = aasProvider.getValue("/aas/submodels/testSubmodel/submodel");
		ISubmodel persistentSM = Submodel.createAsFacade((Map<String, Object>) smObject);
		return persistentSM;
	}
	
	private static void startAASServerComponent(BaSyxContextConfiguration contextConfig,
			BaSyxAASServerConfiguration aasContextConfig) {
		aasServerComponent = new AASServerComponent(contextConfig, aasContextConfig);
		
//		aasServerComponent.setAASBundle(new AASBundle(aas, Collections.singleton(submodel)));
		
		aasServerComponent.startComponent();
	}
	
	private static void startRegistryServer() {
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration(RegistryBackend.INMEMORY);
		
		registryComponent = new RegistryComponent(configureBasyxContext("registryContext.properties"), registryConfig);
		registryComponent.startComponent();
	}
	
	private static IAASRegistry createAASRegistryProxy(BaSyxAASServerConfiguration aasContextConfig) {
		return new AASRegistryProxy(REGISTRYPATH);
	}
	
	private static BaSyxContextConfiguration configureBasyxContext(String path) {
		BaSyxContextConfiguration contextConfig= new BaSyxContextConfiguration();
		contextConfig.loadFromResource(path);
		return contextConfig;
	}

	private static BaSyxAASServerConfiguration configureAASServer() {
		BaSyxAASServerConfiguration aasContextConfig = new BaSyxAASServerConfiguration();
		aasContextConfig.loadFromResource("aasServerConfig.properties");
		return aasContextConfig;
	}
	
	private static Property createDelegatedProperty() {
		Property delegated = new Property("delegated", ValueType.String);
		delegated.setQualifiers(Collections.singleton(createQualifier(SERVER_URL, ENDPOINT)));
		return delegated;
	}
	
	private static IQualifier createQualifier(String serverUrl, String endpoint) {
		return PropertyDelegationManager.createDelegationQualifier(serverUrl + endpoint);
	}
}
