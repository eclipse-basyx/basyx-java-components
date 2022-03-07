/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;

/**
 * 
 * Feature for Authorization of Submodel and Shell access
 * 
 * @author fischer, fried
 *
 */
public class AuthorizedAASServerFeature implements IAASServerFeature {

	public AuthorizedAASServerFeature() {
	}

	@Override
	public void initialize() {
	}

	@Override
	public void cleanUp() {
	}

	@Override
	public IAASServerDecorator getDecorator() {
		return new AuthorizedAASServerDecorator();
	}

}
