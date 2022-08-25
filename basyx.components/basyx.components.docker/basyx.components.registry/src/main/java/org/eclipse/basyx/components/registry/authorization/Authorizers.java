package org.eclipse.basyx.components.registry.authorization;

import org.eclipse.basyx.extensions.aas.registration.authorization.IAASRegistryAuthorizer;

public class Authorizers<SubjectInformationType> {
  private final IAASRegistryAuthorizer<SubjectInformationType> aasRegistryAuthorizer;

  public IAASRegistryAuthorizer<SubjectInformationType> getAasRegistryAuthorizer() {
    return aasRegistryAuthorizer;
  }

  public Authorizers(
      final IAASRegistryAuthorizer<SubjectInformationType> aasRegistryAuthorizer
  ) {
    this.aasRegistryAuthorizer = aasRegistryAuthorizer;
  }
}
