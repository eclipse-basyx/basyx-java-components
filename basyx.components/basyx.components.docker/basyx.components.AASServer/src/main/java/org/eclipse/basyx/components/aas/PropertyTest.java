package org.eclipse.basyx.components.aas;

import java.lang.ProcessHandle.Info;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.esf.SigPolicyQualifiers;
import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPI;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElement;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.AASLambdaPropertyHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.milo.opcua.stack.core.types.structured.GetEndpointsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyTest {
	private static Logger logger = LoggerFactory.getLogger(PropertyTest.class);
	
	public static void main(String[] args) throws InterruptedException {
		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell();
		assetAdministrationShell.setIdShort("aasIdShort");
		Property prop = new Property("test", 123);
		Collection<IConstraint> qualifierCollection = new ArrayList<>();
		qualifierCollection.add(new Qualifier("delegatedTo", "http://127.0.0.1:8083/delegator", "anyType", null));
		
		prop.setQualifiers(qualifierCollection);
        AASLambdaPropertyHelper.setLambdaValue(prop, PropertyTest::getPropertyValue, null);
        
        Submodel testSm = new Submodel("testSm", new CustomId("Test"));
        SubmodelElementCollection smc = new SubmodelElementCollection("smc");
        SubmodelElementCollection subSmc = new SubmodelElementCollection("subsmc"); 
        subSmc.addSubmodelElement(prop);
        smc.addSubmodelElement(subSmc);
        testSm.addSubmodelElement(smc);
        
        assetAdministrationShell.addSubmodel(testSm);
        
        ISubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(testSm));
//        System.out.println(api.getSubmodel().getSubmodelElement("smc"));
        
        ISubmodelElement smElement = api.getSubmodel().getSubmodelElement("smc");
        logger.info("Get SM : {}", api.getSubmodel());
        Map<String, ISubmodelElement> smElem = api.getSubmodel().getSubmodelElements();
        recursiveMet(smElem);
        
//        System.out.println(api.getSubmodel().getSubmodelElement("smc").getValue());
        
        
        System.out.println("Going to sleep");
        Thread.sleep(30000);
        
        System.out.println("Woke up from sleep");
        
        System.out.println(api.getSubmodel().getSubmodelElement("smc").getValue());
        
//        Collection<ISubmodelElement> submodelElements = api.getSubmodelElements();
////        Collection<ISubmodelElementCollection> smCollections = api.getSubmodelElements().stream().filter(e -> e instanceof ISubmodelElementCollection smec).map(e -> (SubmodelElementCollection) e).collect(Collectors.toList());
////		logger.info("smCol = " + smCollections.toString());
//        if(!submodelElements.isEmpty()) {
//			for (ISubmodelElement submodelElement : submodelElements) {
//				if(submodelElement instanceof ISubmodelElementCollection) {
//					
//					logger.info("Instance of SM Elem Collection");
//				}
//				logger.info("SM Elem : " + submodelElement.getIdShort());
////				logger.info("SM Elem : " + submodelElement.getValue());
//				logger.info("SM Elem : " + submodelElement.getModelType());
//			}
//		}
        
//		}
//        System.out.println(api.getSubmodel().getSubmodelElement("test"));
	}

	private static void recursiveMet(Map<String, ISubmodelElement> smElem) {
		for (var entry : smElem.entrySet()) {
            logger.info(entry.getKey() + "/" + entry.getValue());
            if(entry.getValue() instanceof SubmodelElementCollection) {
            	logger.info("instance of SM ELEM COLL");
            	SubmodelElementCollection smElementCollection = (SubmodelElementCollection) entry.getValue();
            	smElementCollection.getSubmodelElements();
            	recursiveMet(smElementCollection.getSubmodelElements());
            } else if(entry.getValue() instanceof SubmodelElement) {
            	logger.info("instance of SM ELEM");
            	handleSubmodelElementProperty(entry.getValue());
            }
        }
	}

//	private static void fetchSm(AssetAdministrationShell assetAdministrationShell) {
//		IAASAPI aIaasapi = new VABAASAPI(new VABLambdaProvider(assetAdministrationShell));
//		System.out.println("From AAS : " + aIaasapi.getAAS().getSubmodels());
//		
//	}

	private static void handleSubmodelElementProperty(ISubmodelElement value) {
		// TODO Auto-generated method stub
		if(!(value instanceof Property)) {
			return;
		}
		
		Collection<IConstraint> qualifiers = value.getQualifiers();
		for (IConstraint iConstraint : qualifiers) {
			if(iConstraint instanceof Qualifier) {
				logger.info("instance of Qualifier");
				if(((Qualifier) iConstraint).getType().equals("delegatedTo") ) {
					logger.info("Qualifier contains delgated to");
					logger.info("Qualifier Value : " + ((Qualifier) iConstraint).getType());
					logger.info("Qualifier Value : " + ((Qualifier) iConstraint).getValue());
				}
			}
		}
//		if(qualifiers.contains("delegatedTo")) {
//			logger.info("Qualifier contains delgated to");
//		}
	}

	private static String getPropertyValue() {
		URL url = null;
		try {
			url = new URL("https://reqres.in/api/users/2");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Authority : " + url.getRef());
		System.out.println("Address : " + url.getHost());
		System.out.println("Path : " + url.getPath());
		HTTPConnector connector = new HTTPConnector("https://o8mwz.mocklab.io");
//		System.out.println("Value : " + connector.getValue("/api/users/2"));
		return connector.getValue("/v1/contacts/1234");
	}

}
