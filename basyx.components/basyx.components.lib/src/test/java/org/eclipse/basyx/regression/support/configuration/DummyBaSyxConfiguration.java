package org.eclipse.basyx.regression.support.configuration;

import java.util.HashMap;

import org.eclipse.basyx.components.configuration.BaSyxConfiguration;

/**
 * A sample BaSyx configuration which created to test reading the environment
 * variables set up in different styles
 * 
 * @author zhangzai
 *
 */
public class DummyBaSyxConfiguration extends BaSyxConfiguration {
	public static final String ENV_PREFIX = "BaSyxAAS_";

	public static final String BACKEND = "aas.backend";
	public static final String EVENTS = "aas.events";
	public static final String AASX_UPLOAD = "aas.aasxUpload";

	public DummyBaSyxConfiguration() {
		super(new HashMap<String, String>());
	}

	public void loadFromEnvironmentVariables() {
		String[] properties = { BACKEND, EVENTS, AASX_UPLOAD };
		loadFromEnvironmentVariables(ENV_PREFIX, properties);
	}

}
