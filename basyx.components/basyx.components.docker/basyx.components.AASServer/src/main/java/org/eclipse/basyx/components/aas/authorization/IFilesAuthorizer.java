package org.eclipse.basyx.components.aas.authorization;

import java.nio.file.Path;
import org.eclipse.basyx.extensions.shared.authorization.InhibitException;

public interface IFilesAuthorizer<SubjectInformationType> {
  void enforceDownloadFile(
      final SubjectInformationType subjectInformation,
      final Path path
  ) throws InhibitException;
}
