package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.codehaus.janino.util.DeepCopier;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.aas.delegation.DelegationAASServerDecorator;
import org.eclipse.basyx.components.aas.delegation.DelegationAASServerFeature;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.extensions.submodel.delegation.DelegationSubmodelAPI;
import org.eclipse.basyx.extensions.submodel.delegation.DelegatingDecoratingSubmodelAPIFactory;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
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
	ISubmodelAPIFactory submodelAPIFactory;
	
	@BeforeClass
	public static void init() {
		Submodel submodel = new Submodel();
		Property prop = new Property("test", "abc");
		Collection<IConstraint> qualifierCollection = new ArrayList<>();
		qualifierCollection.add(new Qualifier("delegatedTo", "https://www.boredapi.com/api/activity?participants=1", "anyType", null));
		prop.setQualifiers(qualifierCollection);
		submodel.addSubmodelElement(prop);
		
		DelegatingDecoratingSubmodelAPIFactory delegationDecoratingSubmodelAPIFactory = new DelegatingDecoratingSubmodelAPIFactory(new VABSubmodelAPIFactory());
		
		submodelAPI = delegationDecoratingSubmodelAPIFactory.getSubmodelAPI(submodel);
	}
	
	@Test
	public void currentValueFromDelegatedEndpoint() {
		ISubmodelElement smc = (ISubmodelElement) submodelAPI.getSubmodel().getSubmodelElement("test");
		logger.info("Current Value SMC : {} ", smc);
		logger.info("Current Value : {} ", smc.getValue());
		
		assertEquals("abc", smc.getValue());
	}
}
