package org.eclipse.basyx.components.registry.authorization;

import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.extensions.shared.authorization.KeycloakService;
import org.eclipse.basyx.vab.protocol.http.server.JwtBearerTokenAuthenticationConfiguration;

public class KeycloakJwtBearerTokenAuthenticationConfigurationProvider implements IJwtBearerTokenAuthenticationConfigurationProvider {
  @Override
  public JwtBearerTokenAuthenticationConfiguration get(BaSyxRegistryConfiguration registryConfig) {
    final String serverUrl = registryConfig.getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakServerUrl();
    final String realm = registryConfig.getAuthorizationStrategyJwtBearerTokenAuthenticationConfigurationProviderKeycloakRealm();

    final KeycloakService keycloakService = new KeycloakService(serverUrl, realm);

    return keycloakService.createJwtBearerTokenAuthenticationConfiguration();
  }
}
