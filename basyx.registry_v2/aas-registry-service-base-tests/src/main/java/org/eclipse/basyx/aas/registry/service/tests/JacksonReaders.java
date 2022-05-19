/*******************************************************************************
 * Copyright (C) 2022 DFKI GmbH
 * Author: Gerhard Sonnenberg (gerhard.sonnenberg@dfki.de)
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
package org.eclipse.basyx.aas.registry.service.tests;

import org.eclipse.basyx.aas.registry.events.RegistryEvent;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.aas.registry.model.SubmodelDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.Getter;
import lombok.Setter;

public class JacksonReaders {

	@Setter
	private ObjectMapper mapper;

	@Getter(lazy = true)
	private final ObjectReader shellDescriptorListReader = mapper.readerForListOf(AssetAdministrationShellDescriptor.class);

	@Getter(lazy = true)
	private final ObjectReader subModelListReader = mapper.readerForListOf(SubmodelDescriptor.class);

	@Getter(lazy = true)
	private final ObjectReader subModelReader = mapper.readerFor(SubmodelDescriptor.class);

	@Getter(lazy = true)
	private final ObjectReader shellDescriptorReader = mapper.readerFor(AssetAdministrationShellDescriptor.class);

	@Getter(lazy = true)
	private final ObjectReader registryEventReader = mapper.readerFor(RegistryEvent.class);

	@Autowired
	public JacksonReaders(ObjectMapper mapper) {
		this.mapper = mapper;
	}

}
