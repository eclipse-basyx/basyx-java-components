package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;

public interface IAuthorizersProvider<SubjectInformationType> {
  Authorizers<SubjectInformationType> get(BaSyxAASServerConfiguration aasServerConfiguration);
}
