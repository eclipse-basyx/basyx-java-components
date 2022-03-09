/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.aas.aascomponent;

import org.eclipse.basyx.aas.aggregator.api.IAASAggregator;

/**
 * Interface for providing an AASAggregator to the AASServerComponent
 * 
 * @author fried
 *
 */
public interface IAASServerComponentAggregatorFactory {
	public IAASAggregator create();
}
