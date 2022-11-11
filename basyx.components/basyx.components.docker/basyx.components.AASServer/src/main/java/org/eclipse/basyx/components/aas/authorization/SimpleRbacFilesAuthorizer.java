package org.eclipse.basyx.components.aas.authorization;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Supplier;
import org.eclipse.basyx.extensions.shared.authorization.GrantedAuthorityUtil;
import org.eclipse.basyx.extensions.shared.authorization.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IRbacRuleChecker;
import org.eclipse.basyx.extensions.shared.authorization.IRoleAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.IdUtil;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;
import org.eclipse.basyx.extensions.shared.authorization.SimpleRbacUtil;
import org.eclipse.basyx.extensions.submodel.aggregator.authorization.SubmodelAggregatorScopes;
import org.eclipse.basyx.submodel.metamodel.api.ISubmodel;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IIdentifier;

public class SimpleRbacFilesAuthorizer<SubjectInformationType> implements IFilesAuthorizer<SubjectInformationType> {
  protected IRbacRuleChecker rbacRuleChecker;
  protected IRoleAuthenticator<SubjectInformationType> roleAuthenticator;

  public SimpleRbacFilesAuthorizer(final IRbacRuleChecker rbacRuleChecker, final IRoleAuthenticator<SubjectInformationType> roleAuthenticator) {
    this.rbacRuleChecker = rbacRuleChecker;
    this.roleAuthenticator = roleAuthenticator;
  }

  @Override
  public void enforceDownloadFile(
      SubjectInformationType subjectInformation,
      Path path
  ) throws InhibitException {
    SimpleRbacUtil.checkRule(rbacRuleChecker, roleAuthenticator, subjectInformation, FilesAuthorizerScopes.READ_SCOPE, new PathTargetInformation(path));
  }
}
