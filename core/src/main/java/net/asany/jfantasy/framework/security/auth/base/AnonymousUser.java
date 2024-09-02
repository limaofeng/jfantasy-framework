package net.asany.jfantasy.framework.security.auth.base;

import net.asany.jfantasy.framework.security.auth.core.ClientDetails;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

/** 匿名用户 */
public record AnonymousUser(ClientDetails clientDetails) implements AuthenticatedPrincipal {

  @Override
  public Long getId() {
    return 0L;
  }

  @Override
  public String getName() {
    return "Anonymous";
  }
}
