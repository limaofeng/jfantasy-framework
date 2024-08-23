package net.asany.jfantasy.framework.security;

import net.asany.jfantasy.framework.security.authentication.Authentication;

public interface TenantService {

  String getCurrentTenantId(String clientId);

  String getCurrentTenantId(Authentication authentication);

  boolean isTenantAllowedForClient(String clientId, String tenantId);
}
