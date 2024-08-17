package net.asany.jfantasy.framework.security.auth.apikey;

import java.time.Instant;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.core.*;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * ApiKeyServices
 *
 * <p>
 */
@Slf4j
public class ApiKeyServices
    implements AuthorizationServerTokenServices<ApiKey>, ResourceServerTokenServices<ApiKey> {

  private final TokenStore<ApiKey> tokenStore;
  private final ClientDetailsService clientDetailsService;

  public ApiKeyServices(TokenStore<ApiKey> tokenStore, ClientDetailsService clientDetailsService) {
    this.tokenStore = tokenStore;
    this.clientDetailsService = clientDetailsService;
  }

  @Override
  public AuthenticationToken<ApiKey> loadAuthentication(AuthenticationToken<String> accessToken) {
    //    return loadAuthentication(accessToken.getToken().getTokenValue());
    return null;
  }

  @Override
  public AuthenticationToken<ApiKey> loadAuthentication(String accessToken) {
    ApiKey token = this.readAccessToken(accessToken);
    if (token == null) {
      return null;
    }
    return this.tokenStore.readAuthentication(token.getTokenValue());
  }

  @Override
  public ApiKey readAccessToken(AuthenticationToken accessToken) {
    return readAccessToken("");
  }

  @Override
  public ApiKey readAccessToken(String apiKey) {
    try {
      // 获取令牌
      ApiKey accessToken = this.tokenStore.readAccessToken(apiKey);
      if (accessToken == null) {
        throw new InvalidTokenException("无效的 Token");
      }
      return accessToken;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public ApiKey createAccessToken(Authentication authentication) {
    DefaultAuthenticationDetails details = authentication.getDetails();
    ClientDetails clientDetails =
        this.clientDetailsService.loadClientByClientId(details.getClientId());

    Instant issuedAt = Instant.now();
    Instant expiresAt = details.getExpiresAt();

    String tokenValue = generateApiKey();

    ApiKey apiKey = new ApiKey(details.getClientId(), tokenValue, issuedAt, expiresAt);

    tokenStore.storeAccessToken(apiKey, authentication);

    return apiKey;
  }

  public String generateApiKey() {
    String sourceKey = StringUtil.generateNonceString(22);
    return "ak-" + Base64.getEncoder().encodeToString(sourceKey.getBytes());
  }

  @Override
  public ApiKey getAccessToken(Authentication authentication) {
    return null;
  }
}
