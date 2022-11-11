package org.eclipse.basyx.components.aas.authorization;

import org.eclipse.basyx.extensions.shared.authorization.ISubjectInformationProvider;

public class AuthorizedDefaultServletParams<SubjectInformationType> {
  private final IFilesAuthorizer<SubjectInformationType> filesAuthorizer;
  private final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider;

  public IFilesAuthorizer<SubjectInformationType> getFilesAuthorizer() {
    return filesAuthorizer;
  }

  public ISubjectInformationProvider<SubjectInformationType> getSubjectInformationProvider() {
    return subjectInformationProvider;
  }

  public AuthorizedDefaultServletParams(final IFilesAuthorizer<SubjectInformationType> filesAuthorizer, final ISubjectInformationProvider<SubjectInformationType> subjectInformationProvider) {
    this.filesAuthorizer = filesAuthorizer;
    this.subjectInformationProvider = subjectInformationProvider;
  }
}
