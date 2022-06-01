/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/

 *
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/

package basyx.components.updater.core.configuration.factory;

import java.util.List;

import basyx.components.updater.core.configuration.route.sources.DelegatorConfiguration;

/**
 * A generic implementation of delegators configuration factory
 *
 * @author haque
 *
 */
public class DelegatorsConfigurationFactory extends ConfigurationFactory {
	private static final String DEFAULT_FILE_PATH = "delegator.json";

	public DelegatorsConfigurationFactory(String filePath, ClassLoader loader, Class<?> mapperClass) {
		super(filePath, loader, mapperClass);
	}

	public DelegatorsConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, DelegatorConfiguration.class);
	}

	/**
	 * This constructor uses the default path {@link #DEFAULT_FILE_PATH}
	 *
	 * @param loader
	 */
	public DelegatorsConfigurationFactory(ClassLoader loader) {
		super(DEFAULT_FILE_PATH, loader, DelegatorConfiguration.class);
	}

	/**
	 * @deprecated use the {@link #create()} method instead
	 */
	@Deprecated
	public List<DelegatorConfiguration> getDelegatorConfigurations() {
		return this.create();
	}

	@SuppressWarnings("unchecked")
	public List<DelegatorConfiguration> create() {
		return (List<DelegatorConfiguration>) getConfigurationLoader().loadListConfiguration();
	}
}
