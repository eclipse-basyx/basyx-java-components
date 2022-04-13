/*******************************************************************************
 * Copyright (C) 2021 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.basyx.components.configuration.builder;

import org.eclipse.basyx.components.configuration.ConfigurableComponent;

/**
 * Base class for component configuration builders
 * 
 * If the end() operation of a component configuration builder is invoked, and
 * no sufficient configuration is provided, the component will throw an
 * InsufficientConfigurationDataException.
 * 
 * @author kuhn
 *
 */
public abstract class BaSyxConfigurationBuilder<ParentBuilderType> {

	/**
	 * Configured component
	 */
	@SuppressWarnings("rawtypes")
	protected ConfigurableComponent configuredComponent = null;

	/**
	 * Parent builder
	 */
	protected ParentBuilderType parentBuilder = null;

	/**
	 * End configuration
	 * 
	 * This base implementation invokes the {@literal <<<>>>} operation of the
	 * configured component if configured component is not null. Only set the
	 * configured component for the top level builder, not for nested builders.
	 */
	@SuppressWarnings("unchecked")
	public ParentBuilderType end() {
		// Null pointer check
		// - If configured component is null, this is the case for nested builders,
		// parent builder
		// must not be null.
		if (configuredComponent == null)
			return parentBuilder;

		// Configure configured component
		configuredComponent.configureComponent(this);

		// Return null - if a configured component is set, no parent builder is set
		return null;
	}

	/**
	 * Set reference to configured component
	 */
	public void setConfiguredComponent(ConfigurableComponent<?> component) {
		configuredComponent = component;
	}

	/**
	 * Set parent builder
	 */
	public void setParentBuilder(ParentBuilderType parentBldr) {
		parentBuilder = parentBldr;
	}
}
