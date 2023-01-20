/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
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

package org.eclipse.digitaltwin.basyx.submodelrepository.core;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.digitaltwin.aas4j.v3.model.Submodel;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepository;
import org.eclipse.digitaltwin.basyx.submodelrepository.SubmodelRepositoryFactory;
import org.junit.Test;

/**
 * Testsuite for implementations of the SubmodelRepository interface
 * 
 * @author schnicke
 *
 */
public abstract class SubmodelRepositorySuite {
	protected abstract SubmodelRepositoryFactory getSubmodelRepositoryFactory();
	protected abstract SubmodelRepositoryFactory getSubmodelRepositoryFactory(Collection<Submodel> submodels);

	@Test
	public void getAllSubmodelsPreconfigured() {
		Collection<Submodel> expectedSubmodels = DummySubmodelFactory.getSubmodels();

		SubmodelRepository repo = getSubmodelRepositoryFactory(expectedSubmodels).create();
		Collection<Submodel> submodels = repo.getAllSubmodels();

		assertEquals(expectedSubmodels, submodels);
	}

	@Test
	public void getAllSubmodelsEmpty() {
		SubmodelRepository repo = getSubmodelRepositoryFactory().create();
		Collection<Submodel> submodels = repo.getAllSubmodels();

		assertEquals(Collections.emptySet(), submodels);
	}


}
