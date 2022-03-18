package org.eclipse.basyx.regression.components.configuration;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.basyx.regression.support.configuration.DummyBaSyxConfiguration;
import org.junit.Test;

/**
 * Test loading environment variables. BaSyx should accept any of both styles
 * (style with dot, style with underscore and lower case). If both styles are
 * used at the same time, an runtime exception is expected
 * 
 * @author zhangzai
 *
 */
public class TestLoadEnvironmentVariables {
	private static final String DEPRECATED_NAMEING_CONVENTION = "deprecatedNamingConvention";
	private static final String LOWERCASE_NAMING_CONVENTION = "lowercaseNamingConvention";
	private static final String BOTH1 = "both1";
	private static final String BOTH2 = "both2";

	private DummyBaSyxConfiguration basyxServerConfig = new DummyBaSyxConfiguration();

	/**
	 * Setup environment variables only for testing purpose.
	 * 
	 * @param variant
	 *            - there three variants to be tested: environment variable with
	 *            dot; environment variable with underscore; or both
	 * @throws Exception
	 */
	public void setup(String variant) throws Exception {
		Map<String, String> variables = new HashMap<>();
		if (variant.equals(DEPRECATED_NAMEING_CONVENTION)) {
			variables.put("BaSyxAAS_aas.backend", "InMemory");
			variables.put("BaSyxAAS_aas.events", "MQTT");
			variables.put("BaSyxAAS_aas.aasxUpload", "Enabled");
		} else if (variant.equals(LOWERCASE_NAMING_CONVENTION)) {
			variables.put("basyxaas_aas_backend", "InMemory");
			variables.put("basyxaas_aas_events", "MQTT");
			variables.put("basyxaas_aas_aasxupload", "Enabled");
		} else if (variant.equals(BOTH1)) {
			variables.put("BaSyxAAS_aas.backend", "InMemory");
			variables.put("BaSyxAAS_aas_events", "MQTT");
			variables.put("BaSyxAAS_aas_aasxUpload", "Enabled");
		} else if (variant.equals(BOTH2)) {
			variables.put("BaSyxAAS_aas_backend", "InMemory");
			variables.put("basyxaas_aas_events", "MQTT");
			variables.put("basyxaas_aas_aasxUpload", "Enabled");
		}
		setEnvironmentVariablesForTesting(variables);
	}

	@Test
	public void testReadEnvironmentVariablesWithunderscore() throws Exception {
		setup(LOWERCASE_NAMING_CONVENTION);
		compareProperties();
	}

	@Test
	public void testReadEnvironmentVariablesWithDot() throws Exception {
		setup(DEPRECATED_NAMEING_CONVENTION);
		compareProperties();
	}

	@Test(expected = RuntimeException.class)
	public void testSettingBothVariantsCase1() throws Exception {
		setup(BOTH1);
		basyxServerConfig.loadFromEnvironmentVariables();
	}

	@Test(expected = RuntimeException.class)
	public void testSettingBothVariantsCase2() throws Exception {
		setup(BOTH2);
		basyxServerConfig.loadFromEnvironmentVariables();
	}

	private void compareProperties() {
		basyxServerConfig.loadFromEnvironmentVariables();
		String backendProperty = basyxServerConfig.getProperty("aas.backend");
		String eventProperty = basyxServerConfig.getProperty("aas.events");
		String aasxProperty = basyxServerConfig.getProperty("aas.aasxUpload");
		assertEquals("InMemory", backendProperty);
		assertEquals("MQTT", eventProperty);
		assertEquals("Enabled", aasxProperty);
	}

	/**
	 * Set environment variables for testing purposes. This method changes the
	 * environment variables only in memory. see https://stackoverflow.com/a/7201825
	 * 
	 * @param newenv
	 *            : new environment variables to be set
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setEnvironmentVariablesForTesting(Map<String, String> newenv) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		try {
			Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
			Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
			theEnvironmentField.setAccessible(true);
			Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
			env.clear();
			env.putAll(newenv);
			Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
			theCaseInsensitiveEnvironmentField.setAccessible(true);
			Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
			cienv.clear();
			cienv.putAll(newenv);
		} catch (NoSuchFieldException e) {
			Class[] classes = Collections.class.getDeclaredClasses();
			Map<String, String> env = System.getenv();
			for (Class cl : classes) {
				if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
					Field field = cl.getDeclaredField("m");
					field.setAccessible(true);
					Object obj = field.get(env);
					Map<String, String> map = (Map<String, String>) obj;
					map.clear();
					map.putAll(newenv);
				}
			}
		}
	}
}
