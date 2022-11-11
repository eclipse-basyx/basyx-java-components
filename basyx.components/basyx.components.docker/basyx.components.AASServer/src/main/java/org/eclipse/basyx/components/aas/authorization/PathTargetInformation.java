package org.eclipse.basyx.components.aas.authorization;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.eclipse.basyx.extensions.shared.authorization.TargetInformation;

public class PathTargetInformation extends TargetInformation {
  public Path getPath() {
    return Paths.get(get("path"));
  }

  public PathTargetInformation(final Path path) {
    put("path", path.toString());
  }
}
