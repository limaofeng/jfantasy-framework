package net.asany.jfantasy.framework.security.core.user;

import net.asany.jfantasy.framework.security.auth.core.ClientDetails;

public class OAuth2Principal implements OAuth2User {

  private final String subject;
  private final ClientDetails clientDetails;

  public OAuth2Principal(ClientDetails clientDetails) {
    this.subject = clientDetails.getSubject();
    this.clientDetails = clientDetails;
  }

  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public String getName() {
    return this.clientDetails.getName();
  }
}
