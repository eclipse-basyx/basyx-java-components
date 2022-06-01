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

import basyx.components.updater.core.configuration.route.sources.DelegatorConfiguration;

/**
 * A default configuration factory for Delegators from a default path
 *
 * @deprecated Use the {@link DelegatorsConfigurationFactory} instead
 * @author haque
 *
 */
@Deprecated
public class DefaultDelegatorsConfigurationFactory extends DelegatorsConfigurationFactory {
	private static final String FILE_PATH = "delegator.json";

	/**
	 * @deprecated use the
	 *             {@link DelegatorsConfigurationFactory#DelegatorsConfigurationFactory(ClassLoader)}
	 *             instead
	 *
	 * @param loader
	 */
	@Deprecated
	public DefaultDelegatorsConfigurationFactory(ClassLoader loader) {
		super(FILE_PATH, loader, DelegatorConfiguration.class);
	}

	/**
	 * @deprecated use the
	 *             {@link DelegatorsConfigurationFactory#DelegatorsConfigurationFactory(String, ClassLoader)}
	 *             instead
	 *
	 * @param filePath
	 * @param loader
	 */
	@Deprecated
	public DefaultDelegatorsConfigurationFactory(String filePath, ClassLoader loader) {
		super(filePath, loader, DelegatorConfiguration.class);
	}
}
