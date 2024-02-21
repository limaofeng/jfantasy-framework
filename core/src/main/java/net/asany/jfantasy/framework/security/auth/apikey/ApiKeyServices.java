package net.asany.jfantasy.framework.security.auth.apikey;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.auth.core.*;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;
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
  public BearerTokenAuthentication loadAuthentication(BearerTokenAuthenticationToken accessToken) {
    return loadAuthentication(accessToken.getToken());
  }

  @Override
  public BearerTokenAuthentication loadAuthentication(String accessToken) {
    ApiKey token = this.readAccessToken(accessToken);
    if (token == null) {
      return null;
    }
    return this.tokenStore.readAuthentication(token.getTokenValue());
  }

  @Override
  public ApiKey readAccessToken(BearerTokenAuthenticationToken accessToken) {
    return readAccessToken(accessToken.getToken());
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
  public ApiKey createAccessToken(AuthenticationToken authentication) {
    DefaultAuthenticationDetails details = authentication.getDetails();
    ClientDetails clientDetails =
        this.clientDetailsService.loadClientByClientId(details.getClientId());

    Integer expires = clientDetails.getTokenExpires(TokenType.API_KEY);

    Instant issuedAt = Instant.now();
    Instant expiresAt = expires != null ? issuedAt.plus(expires, ChronoUnit.MINUTES) : null;

    if (details.getExpiresAt() != null) {
      expiresAt = details.getExpiresAt();
    }

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
  public ApiKey getAccessToken(AuthenticationToken authentication) {
    return null;
  }
}
