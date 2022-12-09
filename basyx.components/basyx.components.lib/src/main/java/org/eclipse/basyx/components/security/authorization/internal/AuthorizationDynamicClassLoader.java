package org.eclipse.basyx.components.security.authorization.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.basyx.components.configuration.BaSyxConfiguration;
import org.eclipse.basyx.extensions.shared.authorization.internal.AuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.AuthenticationGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.internal.JWTAuthenticationContextProvider;
import org.eclipse.basyx.extensions.shared.authorization.internal.KeycloakRoleAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationDynamicClassLoader {
	private static Logger logger = LoggerFactory.getLogger(AuthorizationDynamicClassLoader.class);

	private static Map<String, String> classesBySimpleNameMap = new HashMap<>();

	static {
		classesBySimpleNameMap.put(KeycloakRoleAuthenticator.class.getSimpleName(), KeycloakRoleAuthenticator.class.getName());
		classesBySimpleNameMap.put(AuthenticationGrantedAuthorityAuthenticator.class.getSimpleName(), AuthenticationGrantedAuthorityAuthenticator.class.getName());
		classesBySimpleNameMap.put(JWTAuthenticationContextProvider.class.getSimpleName(), JWTAuthenticationContextProvider.class.getName());
		classesBySimpleNameMap.put(AuthenticationContextProvider.class.getSimpleName(), AuthenticationContextProvider.class.getName());
	}

	private AuthorizationDynamicClassLoader() {
	}

	public static <T> T loadInstanceDynamically(final BaSyxConfiguration config, final String propertyName, final Class<T> requiredInterface) {
		try {
			final String className = config.getProperty(propertyName);
			final String effectiveClassName = classesBySimpleNameMap.getOrDefault(className, className);

			if (effectiveClassName == null) {
				throw new IllegalArgumentException("granted authority subject information provider class is null");
			}

			final Class<?> clazz = Class.forName(effectiveClassName);

			if (!requiredInterface.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException(String.format("given %s -> %s does not implement the interface %s.", propertyName, effectiveClassName, requiredInterface.getName()));
			}

			return (T) clazz.getDeclaredConstructor().newInstance();
		} catch (final ClassNotFoundException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new IllegalArgumentException(e);
		}
	}
}
