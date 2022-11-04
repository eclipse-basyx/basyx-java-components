package org.eclipse.basyx.components.aas;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.basyx.aas.metamodel.api.IAssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.aas.restapi.api.IAASAPI;
import org.eclipse.basyx.aas.restapi.vab.VABAASAPI;
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
	
	public static void main(String[] args) throws InterruptedException {
		AssetAdministrationShell assetAdministrationShell = new AssetAdministrationShell();
		assetAdministrationShell.setIdShort("aasIdShort");
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
        
        assetAdministrationShell.addSubmodel(testSm);
        
        ISubmodelAPI api = new VABSubmodelAPI(new VABLambdaProvider(testSm));
//        System.out.println(api.getSubmodel().getSubmodelElement("smc"));
        
        ISubmodelElement smElement = api.getSubmodel().getSubmodelElement("smc");
        
        System.out.println(api.getSubmodel().getSubmodelElement("smc").getValue());
        
        
        System.out.println("Going to sleep");
//        Thread.sleep(30000);
        
        System.out.println("Woke up from sleep");
        
        System.out.println(api.getSubmodel().getSubmodelElement("smc").getValue());
        
        fetchSm(assetAdministrationShell);
        
//        IProperty property = (IProperty) smElement.getValue();
        
//        System.out.println(smElement.getValue());
        
//        getPropertyValue();
//        
//        for (IConstraint iConstraint : qualifierCollection) {
//			
//		}
//        System.out.println(api.getSubmodel().getSubmodelElement("test"));
	}

	private static void fetchSm(AssetAdministrationShell assetAdministrationShell) {
		IAASAPI aIaasapi = new VABAASAPI(new VABLambdaProvider(assetAdministrationShell));
		System.out.println("From AAS : " + aIaasapi.getAAS().getSubmodels());
		
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
		HTTPConnector connector = new HTTPConnector("http://192.168.0.102:9000");
//		System.out.println("Value : " + connector.getValue("/api/users/2"));
		return connector.getValue("/api/groups");
	}

}
