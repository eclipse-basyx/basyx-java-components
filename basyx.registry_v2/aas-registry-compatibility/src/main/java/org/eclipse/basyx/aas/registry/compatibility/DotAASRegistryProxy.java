/*******************************************************************************
 * Copyright (C) 2022 DFKI GmbH
 * Author: Daniel Porta (daniel.porta@dfki.de)
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
package org.eclipse.basyx.aas.registry.compatibility;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.basyx.aas.metamodel.map.descriptor.AASDescriptor;
import org.eclipse.basyx.aas.metamodel.map.descriptor.SubmodelDescriptor;
import org.eclipse.basyx.aas.registration.api.IAASRegistry;
import org.eclipse.basyx.aas.registry.client.api.RegistryAndDiscoveryInterfaceApi;
import org.eclipse.basyx.aas.registry.model.AssetAdministrationShellDescriptor;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;
import org.eclipse.basyx.vab.exception.provider.ProviderException;
import org.eclipse.basyx.vab.exception.provider.ResourceNotFoundException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.RestClientException;

public class DotAASRegistryProxy implements IAASRegistry {

	private RegistryAndDiscoveryInterfaceApi client;

	public DotAASRegistryProxy(String registryUrl) {
		this.client = new RegistryAndDiscoveryInterfaceApi();
		this.client.getApiClient().setBasePath(registryUrl);
	}

	@Override
	public void register(AASDescriptor aasDescriptor) throws ProviderException {
		try {
			AssetAdministrationShellDescriptor descriptor = DescriptorConversions.toDotaasAASDescriptor(aasDescriptor);
			client.postAssetAdministrationShellDescriptor(descriptor);
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public void register(IIdentifier aasIdentifier, SubmodelDescriptor submodelDescriptor) throws ProviderException {
		org.eclipse.basyx.aas.registry.model.SubmodelDescriptor toPost = DescriptorConversions.toDotaasSubmodelDescriptor(submodelDescriptor);
		try {
			client.postSubmodelDescriptor(toPost, aasIdentifier.getId());
		} catch (NotFound ex) {
			throw new ProviderException("Could not find aasDescriptor: " + aasIdentifier.getId(), ex);
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public void delete(IIdentifier aasIdentifier) throws ProviderException {
		try {
			client.deleteAssetAdministrationShellDescriptorByIdWithHttpInfo(aasIdentifier.getId());
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public void delete(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		try {
			client.deleteSubmodelDescriptorByIdWithHttpInfo(aasIdentifier.getId(), submodelIdentifier.getId());
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public AASDescriptor lookupAAS(IIdentifier aasIdentifier) throws ProviderException {
		try {
			AssetAdministrationShellDescriptor response = client.getAssetAdministrationShellDescriptorById(aasIdentifier.getId());
			return DescriptorConversions.toBasyxAASDescriptor(response);
		} catch (NotFound ex) {
			throw new ResourceNotFoundException(ex);
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public List<AASDescriptor> lookupAll() throws ProviderException {
		try {
			List<AssetAdministrationShellDescriptor> result = client.getAllAssetAdministrationShellDescriptors();
			return result.stream().map(DescriptorConversions::toBasyxAASDescriptor).collect(Collectors.toList());
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public List<SubmodelDescriptor> lookupSubmodels(IIdentifier aasIdentifier) throws ProviderException {
		try {
			List<org.eclipse.basyx.aas.registry.model.SubmodelDescriptor> response = client.getAllSubmodelDescriptors(aasIdentifier.getId());
			return response.stream().map(DescriptorConversions::toBasyxSubmodelDescriptor).collect(Collectors.toList());
		} catch (NotFound ex) {
			throw new ResourceNotFoundException(String.format("Could not retrieve AAS %s", aasIdentifier.getId()));
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

	@Override
	public SubmodelDescriptor lookupSubmodel(IIdentifier aasIdentifier, IIdentifier submodelIdentifier) throws ProviderException {
		try {
			org.eclipse.basyx.aas.registry.model.SubmodelDescriptor response = client.getSubmodelDescriptorById(aasIdentifier.getId(), submodelIdentifier.getId());
			return DescriptorConversions.toBasyxSubmodelDescriptor(response);
		} catch (NotFound ex) {
			throw new ResourceNotFoundException(String.format("Could not retrieve AAS %s or submodel %s", aasIdentifier.getId(), submodelIdentifier.getId()));
		} catch (RestClientException ex) {
			throw new ProviderException(ex);
		}
	}

}
