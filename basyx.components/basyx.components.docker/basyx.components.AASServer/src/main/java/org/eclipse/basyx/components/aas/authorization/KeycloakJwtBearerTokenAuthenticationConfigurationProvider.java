package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.components.aas.configuration.BaSyxAASServerConfiguration;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakService;
import org.eclipse.basyx.vab.protocol.http.server.JwtBearerTokenAuthenticationConfiguration;

public class KeycloakJwtBearerTokenAuthenticationConfigurationProvider implements IJwtBearerTokenAuthenticationConfigurationProvider {
  @Override
  public JwtBearerTokenAuthenticationConfiguration get(BaSyxAASServerConfiguration aasConfig) {
    final String serverUrl = aasConfig.getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakServerUrl();
    final String realm = aasConfig.getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakRealm();

    final KeycloakService keycloakService = new KeycloakService(serverUrl, realm);

    return keycloakService.createJwtBearerTokenAuthenticationConfiguration();
  }
}
