package org.eclipse.basyx.components.aas;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IConstraint;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.dataelement.IProperty;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.qualifier.qualifiable.Qualifier;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.AASLambdaPropertyHelper;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.restapi.api.ISubmodelAPI;
import org.eclipse.basyx.submodel.restapi.vab.VABSubmodelAPI;
import org.eclipse.basyx.vab.modelprovider.lambda.VABLambdaProvider;
import org.eclipse.basyx.vab.protocol.http.connector.HTTPConnector;
import org.eclipse.milo.opcua.stack.core.types.structured.GetEndpointsRequest;

public class PropertyTest {
	
	public static void main(String[] args) {
		Property prop = new Property("test", 123);
		Collection<IConstraint> qualifierCollection = new ArrayList<>();
		qualifierCollection.add(new Qualifier("delegatedTo", "http://127.0.0.1:8083/delegator", "String", null));
		
		prop.setQualifiers(qualifierCollection);
        AASLambdaPropertyHelper.setLambdaValue(prop, PropertyTest::getPropertyValue, null);
        
        Submodel testSm = new Submodel("testSm", new CustomId("Test"));
        SubmodelElementCollection smc = new SubmodelElementCollection("smc");
//        SubmodelElementCollection subSmc = new SubmodelElementCollection("sub-smc");
//        subSmc.addSubmodelElement(prop);
        smc.addSubmodelElement(prop);
        testSm.addSubmodelElement(smc);
        
        ISubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(testSm));
//        System.out.println(api.getSubmodel().getSubmodelElement("smc"));
        
        ISubmodelElement smElement = api.getSubmodel().getSubmodelElement("smc");
        
        System.out.println(api.getSubmodel().getSubmodelElement("smc").getValue());
        
//        IProperty property = (IProperty) smElement.getValue();
        
//        System.out.println(smElement.getValue());
        
//        getPropertyValue();
//        
//        for (IConstraint iConstraint : qualifierCollection) {
//			
//		}
//        System.out.println(api.getSubmodel().getSubmodelElement("test"));
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
		HTTPConnector connector = new HTTPConnector("https://reqres.in");
//		System.out.println("Value : " + connector.getValue("/api/users/2"));
		return connector.getValue("/api/users/2");
	}

}
