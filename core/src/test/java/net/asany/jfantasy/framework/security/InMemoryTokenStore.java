package net.asany.jfantasy.framework.security;

import java.util.Collection;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.AuthRefreshToken;
import net.asany.jfantasy.framework.security.auth.core.AuthToken;
import net.asany.jfantasy.framework.security.auth.core.TokenStore;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import org.apache.commons.collections.map.HashedMap;

public class InMemoryTokenStore implements TokenStore {

  private Map<String, BearerTokenAuthentication> cache = new HashedMap();

  @Override
  public BearerTokenAuthentication readAuthentication(BearerTokenAuthenticationToken token) {
    return null;
  }

  @Override
  public BearerTokenAuthentication readAuthentication(String token) {
    return null;
  }

  @Override
  public void storeAccessToken(AuthToken token, Authentication authentication) {}

  @Override
  public AuthToken readAccessToken(String tokenValue) {
    return null;
  }

  @Override
  public void removeAccessToken(AuthToken token) {}

  @Override
  public void storeRefreshToken(AuthRefreshToken refreshToken, Authentication authentication) {}

  @Override
  public AuthRefreshToken readRefreshToken(String tokenValue) {
    return null;
  }

  @Override
  public BearerTokenAuthentication readAuthenticationForRefreshToken(AuthRefreshToken token) {
    return null;
  }

  @Override
  public void removeRefreshToken(AuthRefreshToken token) {}

  @Override
  public void removeAccessTokenUsingRefreshToken(AuthRefreshToken refreshToken) {}

  @Override
  public AuthToken getAccessToken(BearerTokenAuthentication authentication) {
    return null;
  }

  @Override
  public Collection<AuthToken> findTokensByClientIdAndUserName(String clientId, String userName) {
    return null;
  }

  @Override
  public Collection<AuthToken> findTokensByClientId(String clientId) {
    return null;
  }
}
