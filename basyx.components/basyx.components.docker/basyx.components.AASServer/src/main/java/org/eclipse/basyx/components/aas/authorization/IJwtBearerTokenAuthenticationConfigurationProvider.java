package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.vab.protocol.http.server.JwtBearerTokenAuthenticationConfiguration;

public interface IJwtBearerTokenAuthenticationConfigurationProvider {
  JwtBearerTokenAuthenticationConfiguration get(BaSyxAASServerConfiguration aasConfig);
}
