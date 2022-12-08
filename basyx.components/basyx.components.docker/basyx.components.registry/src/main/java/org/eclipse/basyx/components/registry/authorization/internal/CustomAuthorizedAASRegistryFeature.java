package org.eclipse.basyx.components.registry.authorization.internal;

import org.eclipse.basyx.components.configuration.BaSyxSecurityConfiguration;
import org.eclipse.basyx.extensions.shared.authorization.internal.ISubjectInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Specialization of {@link AuthorizedAASRegistryFeature} for the custom
 * authorization scheme.
 *
 * @author wege
 */
public class CustomAuthorizedAASRegistryFeature<SubjectInformationType> extends AuthorizedAASRegistryFeature {
	private static Logger logger = LoggerFactory.getLogger(CustomAuthorizedAASRegistryFeature.class);

	public CustomAuthorizedAASRegistryFeature(final BaSyxSecurityConfiguration securityConfig) {
		super(securityConfig);
	}

	@Override
	public IAASRegistryDecorator getAASRegistryDecorator() {
		logger.info("use Custom authorization strategy");
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();
		final IAuthorizersProvider<SubjectInformationType> authorizersProvider = getAuthorizersProvider();

		final Authorizers<SubjectInformationType> authorizers = authorizersProvider.get(securityConfig);

		return new AuthorizedAASRegistryDecorator<>(authorizers.getAasRegistryAuthorizer(), subjectInformationProvider);
	}

	@Override
	public ITaggedDirectoryDecorator getTaggedDirectoryDecorator() {
		logger.info("use Custom authorization strategy");
		final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider = getSubjectInformationProvider();
		final IAuthorizersProvider<SubjectInformationType> authorizersProvider = getAuthorizersProvider();

		final Authorizers<SubjectInformationType> authorizers = authorizersProvider.get(securityConfig);

		return new AuthorizedTaggedDirectoryDecorator<>(authorizers.getTaggedDirectoryAuthorizer(), subjectInformationProvider);
	}

	@SuppressWarnings("unchecked")
	private IAuthorizersProvider<SubjectInformationType> getAuthorizersProvider() {
		return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_CUSTOM_AUTHORIZERS_PROVIDER, IAuthorizersProvider.class);
	}

	@SuppressWarnings("unchecked")
	private ISubjectInformationProvider<SubjectInformationType> getSubjectInformationProvider() {
		return securityConfig.loadInstanceDynamically(BaSyxSecurityConfiguration.AUTHORIZATION_STRATEGY_CUSTOM_SUBJECT_INFORMATION_PROVIDER, ISubjectInformationProvider.class);
	}
}
