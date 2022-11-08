package org.eclipse.basyx.regression.AASServer;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.janino.util.DeepCopier;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.delegation.DelegationAASServerDecorator;
import org.eclipse.basyx.components.aas.delegation.DelegationAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.extensions.submodel.delegation.DelegatedSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.delegation.DelegationDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPIFactory;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPIFactory;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAASServerWithDelegation {
	private Logger logger = LoggerFactory.getLogger(TestAASServerWithDelegation.class);
	
	private static ISubmodelAPI submodelAPI;
	private static DelegatedSubmodelAPI delegatedSubmodelAPI;
	
	ISubmodelAPIFactory submodelAPIFactory;
	
	private static AASServerComponent aasServerComponent;
	
	@BeforeClass
	public static void init() {
		Submodel submodel = new Submodel();
		Property prop = new Property("test", 123);
		Collection<IConstraint> qualifierCollection = new ArrayList<>();
		qualifierCollection.add(new Qualifier("delegatedTo", "https://o8mwz.mocklab.io/v1/contacts/1234", "anyType", null));
		prop.setQualifiers(qualifierCollection);
		SubmodelElementCollection smc = new SubmodelElementCollection("smc");
		smc.addSubmodelElement(prop);
		submodel.addSubmodelElement(smc);
//		submodelAPI.addSubmodelElement(smc);
		
		DelegationAASServerFeature delegationAASServerFeature = new DelegationAASServerFeature();
		DelegationAASServerDecorator decorator = (DelegationAASServerDecorator) delegationAASServerFeature.getDecorator();
		DelegationDecoratingSubmodelAPIFactory delegationDecoratingSubmodelAPIFactory = (DelegationDecoratingSubmodelAPIFactory) decorator.decorateSubmodelAPIFactory(new VABSubmodelAPIFactory());
		
		submodelAPI = delegationDecoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
		
//		delegatedSubmodelAPI = new DelegatedSubmodelAPI(submodelAPI);
	}
	
	@Test
	public void currentValueFromDelegatedEndpoint() {
		logger.info("Current Value : {} ", ((SubmodelElementCollection) submodelAPI.getSubmodel().getSubmodelElement("smc")).getSubmodelElement("test").getValue());
	}
	
	private static void startAASServerComponent(BaSyxContextConfiguration contextConfig,
			BaSyxAASServerConfiguration aasContextConfig) {
		aasServerComponent = new AASServerComponent(contextConfig, aasContextConfig);
		aasServerComponent.startComponent();
	}
	
	private static BaSyxAASServerConfiguration configureAASServer() {
		BaSyxAASServerConfiguration aasContextConfig = new BaSyxAASServerConfiguration();
		aasContextConfig.loadFromResource("aasServerPropertyDelegationConfig.properties");
		return aasContextConfig;
	}
	
	private static BaSyxContextConfiguration configureBasyxContext(String path) {
		BaSyxContextConfiguration contextConfig= new BaSyxContextConfiguration();
		contextConfig.loadFromResource(path);
		return contextConfig;
	}
}
