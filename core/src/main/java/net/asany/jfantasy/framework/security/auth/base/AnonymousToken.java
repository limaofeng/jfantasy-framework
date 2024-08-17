package net.asany.jfantasy.framework.security.auth.base;

import net.asany.jfantasy.framework.security.auth.core.AbstractAuthToken;

public class AnonymousToken extends AbstractAuthToken {

  protected AnonymousToken(String clientId, String tokenValue) {
    super(clientId, tokenValue);
  }
}
