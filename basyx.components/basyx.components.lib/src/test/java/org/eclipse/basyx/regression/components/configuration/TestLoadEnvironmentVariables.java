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
	private static final String PROPERTY_VALUE_BACKEND = "InMemory";
	private static final String PROPERTY_VALUE_EVENT = "MQTT";
	private static final String PROPERTY_VALUE_AASXUPLOAD = "Enabled";

	private DummyBaSyxConfiguration basyxServerConfig = new DummyBaSyxConfiguration();

	@Test
	public void testReadEnvironmentVariablesWithLowercaseNamingConvention() throws Exception {
		setupVariantWithLowercaseNamingConvention();
		compareProperties();
	}

	@Test
	public void testReadEnvironmentVariablesWithDeprecatedNamingConvention() throws Exception {
		setupVariantWithDeprecatedNamingConvention();
		compareProperties();
	}

	@Test(expected = RuntimeException.class)
	public void testSettingBothVariantsCaseWithDotAndUnderscore() throws Exception {
		setupVariantMixedUsageWithDotAndUnderscore();
		basyxServerConfig.loadFromEnvironmentVariables();
	}

	@Test(expected = RuntimeException.class)
	public void testSettingBothVariantsCaseWithMixedUpperLowerCase() throws Exception {
		setupVariantMixedUsageWithLowerAndUpperCase();
		basyxServerConfig.loadFromEnvironmentVariables();
	}

	public void setupVariantWithLowercaseNamingConvention() throws Exception {
		Map<String, String> variables = new HashMap<>();
		variables.put("basyxaas_aas_backend", PROPERTY_VALUE_BACKEND);
		variables.put("basyxaas_aas_events", PROPERTY_VALUE_EVENT);
		variables.put("basyxaas_aas_aasxupload", PROPERTY_VALUE_AASXUPLOAD);
		setEnvironmentVariablesForTesting(variables);
	}

	public void setupVariantWithDeprecatedNamingConvention() throws Exception {
		Map<String, String> variables = new HashMap<>();
		variables.put("BaSyxAAS_aas.backend", PROPERTY_VALUE_BACKEND);
		variables.put("BaSyxAAS_aas.events", PROPERTY_VALUE_EVENT);
		variables.put("BaSyxAAS_aas.aasxUpload", PROPERTY_VALUE_AASXUPLOAD);
		setEnvironmentVariablesForTesting(variables);
	}

	public void setupVariantMixedUsageWithLowerAndUpperCase() throws Exception {
		Map<String, String> variables = new HashMap<>();
		variables.put("BaSyxAAS_aas_backend", PROPERTY_VALUE_BACKEND);
		variables.put("basyxaas_aas_events", PROPERTY_VALUE_EVENT);
		variables.put("basyxaas_aas_aasxUpload", PROPERTY_VALUE_AASXUPLOAD);
		setEnvironmentVariablesForTesting(variables);
	}

	public void setupVariantMixedUsageWithDotAndUnderscore() throws Exception {
		Map<String, String> variables = new HashMap<>();
		variables.put("BaSyxAAS_aas.backend", PROPERTY_VALUE_BACKEND);
		variables.put("basyxaas_aas.events", PROPERTY_VALUE_EVENT);
		variables.put("basyxaas_aas_aasxUpload", PROPERTY_VALUE_AASXUPLOAD);
		setEnvironmentVariablesForTesting(variables);
	}

	private void compareProperties() {
		basyxServerConfig.loadFromEnvironmentVariables();
		String backendProperty = basyxServerConfig.getProperty("aas.backend");
		String eventProperty = basyxServerConfig.getProperty("aas.events");
		String aasxProperty = basyxServerConfig.getProperty("aas.aasxUpload");
		assertEquals(PROPERTY_VALUE_BACKEND, backendProperty);
		assertEquals(PROPERTY_VALUE_EVENT, eventProperty);
		assertEquals(PROPERTY_VALUE_AASXUPLOAD, aasxProperty);
	}

	/**
	 * Set environment variables for testing purposes. This method changes the
	 * environment variables only in memory. This is only for testing purpose.
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
	protected void setEnvironmentVariablesForTesting(Map<String, String> newenv) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		try {
			Class<?> processEnvironmentClass = getProcessEnvironmentClass();
			Field theEnvironment = getAccessibleField(processEnvironmentClass, "theEnvironment");
			setNewEnvironmentVariables(theEnvironment, newenv);
			Field theCaseInsensitiveEnvironmentField = getAccessibleField(processEnvironmentClass, "theCaseInsensitiveEnvironment");
			setNewEnvironmentVariables(theCaseInsensitiveEnvironmentField, newenv);
		} catch (NoSuchFieldException e) {
			setVariableToUnmodifiableMap(newenv);
		}
	}

	private void setVariableToUnmodifiableMap(Map<String, String> newenv) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?>[] classes = Collections.class.getDeclaredClasses();
		Map<String, String> currentEnvironmentVariables = System.getenv();
		for (Class<?> cl : classes) {
			if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
				Field field = cl.getDeclaredField("m");
				field.setAccessible(true);
				Object obj = field.get(currentEnvironmentVariables);
				@SuppressWarnings("unchecked")
				Map<String, String> map = (Map<String, String>) obj;
				map.clear();
				map.putAll(newenv);
			}
		}
	}

	private Class<?> getProcessEnvironmentClass() throws ClassNotFoundException {
		Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
		return processEnvironmentClass;
	}

	private Field getAccessibleField(Class<?> processEnvironmentClass, String fieldName) throws NoSuchFieldException, SecurityException {
		Field theEnvironmentField = processEnvironmentClass.getDeclaredField(fieldName);
		theEnvironmentField.setAccessible(true);
		return theEnvironmentField;
	}

	private void setNewEnvironmentVariables(Field field, Map<String, String> newenv) throws IllegalArgumentException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		Map<String, String> env = (Map<String, String>) field.get(null);
		env.clear();
		env.putAll(newenv);
	}

}
