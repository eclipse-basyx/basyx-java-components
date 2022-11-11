package org.eclipse.basyx.components.aas.authorization;


public class FilesAuthorizerScopes {
  private static final String SCOPE_AUTHORITY_PREFIX = "SCOPE_";
  public static final String READ_SCOPE = "urn:org.eclipse.basyx:scope:files:read";
  public static final String READ_AUTHORITY = SCOPE_AUTHORITY_PREFIX + READ_SCOPE;

  private FilesAuthorizerScopes() {}
}
