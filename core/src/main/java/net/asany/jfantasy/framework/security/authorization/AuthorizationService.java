package net.asany.jfantasy.framework.security.authorization;

import net.asany.jfantasy.framework.security.authentication.Authentication;

public interface AuthorizationService {

  boolean hasPermission(String resource, String operation, Authentication authentication);
}
