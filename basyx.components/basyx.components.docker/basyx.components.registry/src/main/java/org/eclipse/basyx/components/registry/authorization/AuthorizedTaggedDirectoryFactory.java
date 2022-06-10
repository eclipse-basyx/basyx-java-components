/*******************************************************************************
 * Copyright (C) 2022 the Eclipse BaSyx Authors
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.basyx.components.registry.authorization;

import org.eclipse.basyx.extensions.aas.directory.tagged.api.IAASTaggedDirectory;
import org.eclipse.basyx.extensions.aas.directory.tagged.authorized.AuthorizedTaggedDirectory;

/**
 * Factory for building a Authorized-Registry model provider for
 * AASTaggedDirectory implementations
 * 
 * @author fried
 * 
 */
public class AuthorizedTaggedDirectoryFactory {

	public IAASTaggedDirectory create(IAASTaggedDirectory decoratedTaggedDirectory) {
		AuthorizedTaggedDirectory authorizedTaggedDirectory = new AuthorizedTaggedDirectory(decoratedTaggedDirectory);
		return authorizedTaggedDirectory;
	}

}
