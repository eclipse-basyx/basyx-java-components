package org.eclipse.basyx.regression.AASServer;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;
import org.eclipse.basyx.aas.aggregator.proxy.AASAggregatorProxy;
import org.eclipse.basyx.aas.bundle.AASBundle;
import org.eclipse.basyx.aas.metamodel.map.AssetAdministrationShell;
import org.eclipse.basyx.aas.metamodel.map.descriptor.CustomId;
import org.eclipse.basyx.components.aas.AASServerComponent;
import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.extensions.shared.delegation.PropertyDelegationManager;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.submodel.metamodel.api.qualifier.qualifiable.IQualifier;
import org.eclipse.basyx.submodel.metamodel.map.Submodel;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.valuetype.ValueType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

public class TestAASServerWithPropertyDelegation {
	private static AASServerComponent aasServerComponent;
	
	private static final int EXPECTED_VALUE = 10;
	private static final int SERVER_PORT = 1080;
	
	private static final String SERVER_IP = "127.0.0.1";
	private static final String SERVER_URL = "http://" + SERVER_IP + ":" + SERVER_PORT;
	private static final String ENDPOINT = "/valueEndpoint";
	private static final String SM_ELEM_IDSHORT = "delegated";
	
	private static IIdentifier aasIdentifier = new CustomId("testAAS");
	private static IIdentifier smIdentifier = new CustomId("testSM");
	private static ClientAndServer mockServer;
	
	@BeforeClass
	public static void init() {
		configureAndStartMockHttpServer();
		
		AASBundle aasBundle = createAASBundle();
		
		createExpectationForMockedGet();
		
		startAASServerComponentWithAASBundle(configureAndGetBasyxContext(), new BaSyxAASServerConfiguration(), aasBundle);
	}
	
	@Test
	public void valueFromDelegatedProperty() {
		IAASAggregator proxy = new AASAggregatorProxy(aasServerComponent.getURL());
		
		Object actualValue = proxy.getAAS(aasIdentifier).getSubmodel(smIdentifier).getSubmodelElement(SM_ELEM_IDSHORT).getValue();
		
		assertEquals(EXPECTED_VALUE, Integer.parseInt(actualValue.toString()));
	}

	private static AASBundle createAASBundle() {
		String submodelIdShort = "testSubmodel";
		
		Property delegatedProperty = createDelegatedProperty();
		
		Submodel submodel = new Submodel(submodelIdShort, smIdentifier);
		submodel.addSubmodelElement(delegatedProperty);
		
		AssetAdministrationShell aas = createAssetAdministrationShell();
		aas.addSubmodel(submodel);
		
		return new AASBundle(aas, Collections.singleton(submodel));
	}

	private static AssetAdministrationShell createAssetAdministrationShell() {
		String aasIdShort = "test";
		
		AssetAdministrationShell  aas = new AssetAdministrationShell();
		aas.setIdentification(aasIdentifier);
		aas.setIdShort(aasIdShort);
		
		return aas;
	}

	private static void configureAndStartMockHttpServer() {
		mockServer = startClientAndServer(SERVER_PORT);
	}
	
	private static void createExpectationForMockedGet() {
		new MockServerClient(SERVER_IP, SERVER_PORT).when(request().withMethod("GET").withPath(ENDPOINT))
				.respond(response().withStatusCode(200)
						.withHeaders(new Header("Content-Type", "text/plain; charset=utf-8"),
								new Header("Cache-Control", "public, max-age=86400"))
						.withBody(Integer.toString(EXPECTED_VALUE)).withDelay(TimeUnit.SECONDS, 1));
	}
	
	private static void startAASServerComponentWithAASBundle(BaSyxContextConfiguration contextConfig,
			BaSyxAASServerConfiguration aasContextConfig, AASBundle aasBundle) {
		aasServerComponent = new AASServerComponent(contextConfig, aasContextConfig);
		
		aasServerComponent.setAASBundle(aasBundle);
		
		aasServerComponent.startComponent();
	}
	
	private static BaSyxContextConfiguration configureAndGetBasyxContext() {
		BaSyxContextConfiguration contextConfig= new BaSyxContextConfiguration();
		contextConfig.setHostname("localhost");
		contextConfig.setContextPath("/aasServer");
		contextConfig.setPort(4001);
		
		return contextConfig;
	}
	
	private static Property createDelegatedProperty() {
		Property delegated = new Property(SM_ELEM_IDSHORT, ValueType.String);
		delegated.setQualifiers(Collections.singleton(createQualifier(SERVER_URL, ENDPOINT)));
		return delegated;
	}
	
	private static IQualifier createQualifier(String serverUrl, String endpoint) {
		return PropertyDelegationManager.createDelegationQualifier(serverUrl + endpoint);
	}
	
	@AfterClass
    public static void stopServer() {
		aasServerComponent.stopComponent();
		
        mockServer.stop();
    }
}
