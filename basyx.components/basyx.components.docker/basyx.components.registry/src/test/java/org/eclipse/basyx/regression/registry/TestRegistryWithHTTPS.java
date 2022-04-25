package org.eclipse.basyx.regression.registry;

import static org.junit.Assert.fail;

import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;
import org.eclipse.basyx.components.registry.RegistryComponent;
import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.testsuite.regression.aas.registration.TestRegistryProviderSuite;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRegistryWithHTTPS extends TestRegistryProviderSuite {
	private static RegistryComponent component;
	private static BaSyxContextConfiguration contextConfig;

	@BeforeClass
	public static void setUpClass() {
		contextConfig = createBaSyxContextConfiguration();
		BaSyxRegistryConfiguration registryConfig = createRegistryConfiguration();

		component = new RegistryComponent(contextConfig, registryConfig);
		component.startComponent();
	}

	private static BaSyxRegistryConfiguration createRegistryConfiguration() {
		BaSyxRegistryConfiguration registryConfig = new BaSyxRegistryConfiguration();
		registryConfig.loadFromResource(BaSyxRegistryConfiguration.DEFAULT_CONFIG_PATH);
		return registryConfig;
	}

	private static BaSyxContextConfiguration createBaSyxContextConfiguration() {
		BaSyxContextConfiguration contextConfig = new BaSyxContextConfiguration();
		contextConfig.loadFromResource(BaSyxContextConfiguration.DEFAULT_CONFIG_PATH);
		contextConfig.setSSLKeyStoreLocation("resources/basyxtest.jks");
		contextConfig.setSSLKeyPassword("pass123");
		return contextConfig;
	}

	@Override
	protected IAASRegistry getRegistryService() {
		return new AASRegistryProxy(contextConfig.getUrl());
	}

	@Test
	public void testWithoutHTTPS() {
		try {
			AASRegistryProxy httpProxy = new AASRegistryProxy(createHTTPUrl(contextConfig.getUrl()));
			httpProxy.lookupAll();
			fail();
		} catch (ProviderException expected) {
		}
	}

	private String createHTTPUrl(String url) {
		if (url.toLowerCase().startsWith("https")) {
			return "http" + url.substring(5, url.length());
		}
		return url;
	}

	@AfterClass
	public static void tearDownClass() {
		component.stopComponent();
	}
}
