/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
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
package org.eclipse.basyx.components.aas.delegation;

import org.eclipse.basyx.components.aas.aascomponent.IAASServerDecorator;
import org.eclipse.basyx.components.aas.aascomponent.IAASServerFeature;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;

/**
 * 
 * Feature for Property Delegation of Submodel
 * 
 * @author danish
 *
 */
public class DelegationAASServerFeature implements IAASServerFeature {

	@Override
	public void initialize() {
		// nothing to do regarding initialization for this feature at the moment
	}

	@Override
	public void cleanUp() {
		// nothing to do regarding cleanup for this feature at the moment
	}

	@Override
	public IAASServerDecorator getDecorator() {
		return new DelegationAASServerDecorator();
	}

	@Override
	public void addToContext(BaSyxContext context) {
		// nothing to add to the context for this feature at the moment
	}

}
