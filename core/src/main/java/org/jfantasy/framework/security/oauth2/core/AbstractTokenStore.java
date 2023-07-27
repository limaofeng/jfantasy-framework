package org.jfantasy.framework.security.oauth2.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.core.SimpleGrantedAuthority;
import org.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import org.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Redis 令牌存储器
 *
 * @author limaofeng
 */
@Slf4j
public abstract class AbstractTokenStore implements TokenStore {

  private final String ASSESS_TOKEN_PREFIX = "assess_token:";
  private final String REFRESH_TOKEN_PREFIX = "refresh_token:";

  private final StringRedisTemplate redisTemplate;
  private final ValueOperations<String, String> valueOperations;

  public AbstractTokenStore(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
    this.valueOperations = redisTemplate.opsForValue();
  }

  @Override
  public BearerTokenAuthentication readAuthentication(BearerTokenAuthenticationToken token) {
    String key = ASSESS_TOKEN_PREFIX + token.getToken();
    String data = redisTemplate.boundValueOps(key).get();
    if (StringUtil.isEmpty(data)) {
      return null;
    }
    OAuth2AccessToken accessToken = buildOauth2AccessToken(data);
    return buildBearerTokenAuthentication(data, accessToken, token.getDetails());
  }

  @Override
  public BearerTokenAuthentication readAuthentication(String token) {
    String key = ASSESS_TOKEN_PREFIX + token;
    String data = redisTemplate.boundValueOps(key).get();
    if (StringUtil.isEmpty(data)) {
      return null;
    }
    OAuth2AccessToken accessToken = buildOauth2AccessToken(data);
    return buildBearerTokenAuthentication(data, accessToken);
  }

  @Override
  public void storeAccessToken(OAuth2AccessToken token, Authentication authentication) {
    String data = buildStoreData(token, authentication);

    String key = ASSESS_TOKEN_PREFIX + token.getTokenValue();

    this.valueOperations.set(key, data);

    if (token.getExpiresAt() != null) {
      redisTemplate.expireAt(key, Date.from(token.getExpiresAt()));
    }
  }

  @Override
  public OAuth2AccessToken readAccessToken(String tokenValue) {
    String key = ASSESS_TOKEN_PREFIX + tokenValue;
    String data = redisTemplate.boundValueOps(key).get();
    if (StringUtil.isEmpty(data)) {
      return null;
    }
    return buildOauth2AccessToken(data);
  }

  @Override
  public void removeAccessToken(OAuth2AccessToken token) {
    this.redisTemplate.delete(ASSESS_TOKEN_PREFIX + token.getTokenValue());
  }

  @Override
  public void storeRefreshToken(OAuth2RefreshToken refreshToken, Authentication authentication) {
    log.warn("未实现 storeRefreshToken 逻辑");
    // String principal = principalToString(authentication);
    //
    // String key = refreshToken.getTokenValue();
    //
    // valueOperations.set(REFRESH_TOKEN_PREFIX + key, principal);
    //
    // long expire = Duration.between(refreshToken.getExpiresAt(),
    // refreshToken.getIssuedAt()).toMinutes();
    // redisTemplate.expire(REFRESH_TOKEN_PREFIX + key, expire, TimeUnit.MINUTES);
  }

  @Override
  public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(
      String clientId, String userName) {
    return null;
  }

  @Override
  public OAuth2RefreshToken readRefreshToken(String tokenValue) {
    String token = redisTemplate.boundValueOps(REFRESH_TOKEN_PREFIX + tokenValue).get();
    return null;
  }

  @Override
  public BearerTokenAuthentication readAuthenticationForRefreshToken(
      OAuth2RefreshToken refreshToken) {
    String principal =
        redisTemplate.boundValueOps(REFRESH_TOKEN_PREFIX + refreshToken.getTokenValue()).get();
    LoginUser user = JSON.deserialize(principal, LoginUser.class);
    OAuth2AccessToken accessToken =
        new OAuth2AccessToken(
            TokenType.TOKEN,
            refreshToken.getTokenValue(),
            refreshToken.getIssuedAt(),
            refreshToken.getExpiresAt());
    assert user != null;
    return new BearerTokenAuthentication(user, accessToken, user.getAuthorities());
  }

  @Override
  public void removeRefreshToken(OAuth2RefreshToken token) {
    redisTemplate.delete(REFRESH_TOKEN_PREFIX + token.getTokenValue());
  }

  @Override
  public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {}

  @Override
  public OAuth2AccessToken getAccessToken(BearerTokenAuthentication authentication) {
    return null;
  }

  @Override
  public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
    return null;
  }

  private String buildStoreData(OAuth2AccessToken token, Authentication authentication) {
    Map<String, Object> data = new HashMap<>();
    Collection<? extends GrantedAuthority> tempGrantedAuthority =
        ObjectUtil.defaultValue(authentication.getAuthorities(), Collections::emptyList);
    Set<String> authorities =
        tempGrantedAuthority.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    data.put("access_token", token);
    data.put("principal", authentication.getPrincipal());
    data.put("authorities", authorities);
    return JSON.serialize(data);
  }

  private OAuth2AccessToken buildOauth2AccessToken(String data) {
    ObjectMapper mapper = JSON.getObjectMapper();
    ReadContext context = JsonPath.parse(data);
    TokenType tokenType =
        mapper.convertValue(context.read("$.access_token.tokenType"), TokenType.class);
    String tokenValue =
        mapper.convertValue(context.read("$.access_token.tokenValue"), String.class);
    String refreshTokenValue =
        mapper.convertValue(context.read("$.access_token.refreshTokenValue"), String.class);
    Set<String> scopes =
        Arrays.stream(mapper.convertValue(context.read("$.access_token.scopes"), String[].class))
            .collect(Collectors.toSet());
    Instant issuedAt = mapper.convertValue(context.read("$.access_token.issuedAt"), Instant.class);
    Instant expiresAt =
        mapper.convertValue(context.read("$.access_token.expiresAt"), Instant.class);

    return new OAuth2AccessToken(
        tokenType, tokenValue, refreshTokenValue, issuedAt, expiresAt, scopes);
  }

  private BearerTokenAuthentication buildBearerTokenAuthentication(
      String data, OAuth2AccessToken accessToken) {
    return buildBearerTokenAuthentication(data, accessToken, null);
  }

  private BearerTokenAuthentication buildBearerTokenAuthentication(
      String data, OAuth2AccessToken accessToken, Object details) {
    ObjectMapper mapper = JSON.getObjectMapper();
    ReadContext context = JsonPath.parse(data);

    LoginUser principal = mapper.convertValue(context.read("$.principal"), LoginUser.class);
    List<GrantedAuthority> authorities =
        new ArrayList<>(
            mapper.convertValue(
                context.read("$.authorities"),
                new TypeReference<List<SimpleGrantedAuthority>>() {}));
    BearerTokenAuthentication bearerTokenAuthentication =
        new BearerTokenAuthentication(principal, accessToken, authorities);
    if (details != null) {
      bearerTokenAuthentication.setDetails(details);
    }
    return bearerTokenAuthentication;
  }
}
