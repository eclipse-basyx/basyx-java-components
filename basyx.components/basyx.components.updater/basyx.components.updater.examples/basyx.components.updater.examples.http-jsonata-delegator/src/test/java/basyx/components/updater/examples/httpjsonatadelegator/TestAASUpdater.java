package basyx.components.updater.examples.httpjsonatadelegator;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import basyx.components.updater.camelhttp.configuration.factory.HttpDefaultConfigurationFactory;
import basyx.components.updater.core.component.UpdaterComponent;
import basyx.components.updater.core.configuration.factory.DefaultDelegatorsConfigurationFactory;
import basyx.components.updater.core.configuration.factory.DefaultRoutesConfigurationFactory;
import basyx.components.updater.core.configuration.route.RoutesConfiguration;
import basyx.components.updater.examples.httpserver.HttpDataSource;
import basyx.components.updater.transformer.cameljsonata.configuration.factory.JsonataDefaultConfigurationFactory;

public class TestAASUpdater {
	private static UpdaterComponent updater;
	private static HttpDataSource httpSource;

	@BeforeClass
	public static void setUp() throws InterruptedException {
		httpSource = new HttpDataSource();
		httpSource.runHttpServer();
	}

	@Test
	public void test() throws Exception {
		System.out.println("STARTING UPDATER");
		ClassLoader loader = TestAASUpdater.class.getClassLoader();
		RoutesConfiguration configuration = new RoutesConfiguration();

		// Extend configutation for connections
		DefaultRoutesConfigurationFactory routesFactory = new DefaultRoutesConfigurationFactory(loader);
		configuration.addRoutes(routesFactory.getRouteConfigurations());

		// Extend configutation for Kafka Source
		HttpDefaultConfigurationFactory httpConfigFactory = new HttpDefaultConfigurationFactory(loader);
		configuration.addDatasources(httpConfigFactory.getDataSourceConfigurations());

		// Extend configuration for Jsonata
		JsonataDefaultConfigurationFactory jsonataConfigFactory = new JsonataDefaultConfigurationFactory(loader);
		configuration.addTransformers(jsonataConfigFactory.getDataTransformerConfigurations());

		// Extend configuration for Delegator
		DefaultDelegatorsConfigurationFactory delegatorConfigFactory = new DefaultDelegatorsConfigurationFactory(loader);
		configuration.addDelegators(delegatorConfigFactory.getDelegatorConfigurations());

		updater = new UpdaterComponent(configuration);
		updater.startComponent();
		System.out.println("DELEGATOR STARTED");
		System.out.println("CALLING ENDPOINT");
		callApiAndCheckResult();
	}

	private void callApiAndCheckResult() throws ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://localhost:8090/valueA");
		CloseableHttpResponse resp = client.execute(request);
	    String content = new String(resp.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);

		System.out.println(content);
		assertEquals("{\"value\":\"336.36\"}", content);
		client.close();
	}

	@AfterClass
	public static void tearDown() {
		httpSource.stopHttpServer();
	}
}
