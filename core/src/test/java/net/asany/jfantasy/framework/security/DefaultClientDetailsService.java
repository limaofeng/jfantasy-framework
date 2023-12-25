package net.asany.jfantasy.framework.security;

import net.asany.jfantasy.framework.security.oauth2.core.ClientDetails;
import net.asany.jfantasy.framework.security.oauth2.core.ClientDetailsService;
import net.asany.jfantasy.framework.security.oauth2.core.ClientRegistrationException;

public class DefaultClientDetailsService implements ClientDetailsService {
  @Override
  public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
    return null;
  }
}
