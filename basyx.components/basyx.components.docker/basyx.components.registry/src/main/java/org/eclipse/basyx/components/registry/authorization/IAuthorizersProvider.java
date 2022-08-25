package org.eclipse.basyx.components.registry.authorization;

import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;

public interface IAuthorizersProvider<SubjectInformationType> {
  Authorizers<SubjectInformationType> get(BaSyxRegistryConfiguration registryConfiguration);
}
