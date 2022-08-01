package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.components.configuration.BaSyxContextConfiguration;

public interface IAuthorizersProvider<SubjectInformationType> {
  Authorizers<SubjectInformationType> get(BaSyxAASServerConfiguration aasServerConfiguration);
}
