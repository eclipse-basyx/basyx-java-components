package org.eclipse.basyx.components.registry.authorization;

import org.eclipse.basyx.components.registry.configuration.BaSyxRegistryConfiguration;
import org.eclipse.basyx.vab.protocol.http.server.JwtBearerTokenAuthenticationConfiguration;

public interface IJwtBearerTokenAuthenticationConfigurationProvider {
  JwtBearerTokenAuthenticationConfiguration get(BaSyxRegistryConfiguration registryConfig);
}
