package org.eclipse.basyx.components.aas.authorization;

import java.nio.file.Path;
import org.eclipse.basyx.extensions.shared.authorization.GrantedAuthorityUtil;
import org.eclipse.basyx.extensions.shared.authorization.IGrantedAuthorityAuthenticator;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;

public class GrantedAuthorityFilesAuthorizer<SubjectInformationType> implements IFilesAuthorizer<SubjectInformationType> {
  protected IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator;

  public GrantedAuthorityFilesAuthorizer(final IGrantedAuthorityAuthenticator<SubjectInformationType> grantedAuthorityAuthenticator) {
    this.grantedAuthorityAuthenticator = grantedAuthorityAuthenticator;
  }

  @Override
  public void enforceDownloadFile(
      SubjectInformationType subjectInformation,
      Path path
  ) throws InhibitException {
    GrantedAuthorityUtil.checkAuthority(grantedAuthorityAuthenticator, subjectInformation, FilesAuthorizerScopes.READ_AUTHORITY);
  }
}
