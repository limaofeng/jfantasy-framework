/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.security.auth.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.authority.SimpleGrantedAuthority;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * Redis 令牌存储器
 *
 * @author limaofeng
 */
@Slf4j
public abstract class AbstractTokenStore<T extends AuthToken> implements TokenStore<T> {

  private final String assess_token_prefix;
  private final String refresh_token_prefix;

  private final StringRedisTemplate redisTemplate;
  private final ValueOperations<String, String> valueOperations;

  protected final ObjectMapper mapper;

  private final Class<T> authTokenClass;

  public AbstractTokenStore(StringRedisTemplate redisTemplate, String redisPrefix) {
    this.redisTemplate = redisTemplate;
    this.valueOperations = redisTemplate.opsForValue();
    this.assess_token_prefix = redisPrefix + ":";
    this.refresh_token_prefix = redisPrefix + ":refresh_token:";
    this.mapper =
        new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .registerModule(new JavaTimeModule());
    this.authTokenClass = (Class<T>) ClassUtil.getSuperClassGenricType(getClass(), 0);
  }

  @Override
  public Authentication readAuthentication(AuthenticationToken<String> token) {
    String key = assess_token_prefix + token.getToken();
    String data = redisTemplate.boundValueOps(key).get();
    if (StringUtil.isEmpty(data)) {
      return null;
    }
    TokenObject tokenObject = buildAuthToken(data);
    return buildAuthenticationToken(data, tokenObject, token.getDetails());
  }

  @Override
  public AuthenticationToken<T> readAuthentication(String token) {
    String key = assess_token_prefix + token;
    String data = redisTemplate.boundValueOps(key).get();
    if (StringUtil.isEmpty(data)) {
      return null;
    }
    TokenObject tokenObject = buildAuthToken(data);
    return buildAuthenticationToken(data, tokenObject);
  }

  @Override
  public void storeAccessToken(T token, Authentication authentication) {
    String data = buildStoreData(token, authentication);

    String key = assess_token_prefix + token.getTokenValue();

    this.valueOperations.set(key, data);

    if (token.getExpiresAt() != null) {
      redisTemplate.expireAt(key, Date.from(token.getExpiresAt()));
    }
  }

  @Override
  public T readAccessToken(String tokenValue) {
    AuthenticationToken<T> tokenAuthentication = this.readAuthentication(tokenValue);
    if (tokenAuthentication == null) {
      return null;
    }
    return tokenAuthentication.getToken();
  }

  @Override
  public void removeAccessToken(T token) {
    this.redisTemplate.delete(assess_token_prefix + token.getTokenValue());
  }

  @Override
  public void storeRefreshToken(AuthRefreshToken refreshToken, Authentication authentication) {
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
  public Collection<AuthToken> findTokensByClientIdAndUserName(String clientId, String userName) {
    return null;
  }

  @Override
  public AuthRefreshToken readRefreshToken(String tokenValue) {

    String token = redisTemplate.boundValueOps(refresh_token_prefix + tokenValue).get();
    return null;
  }

  @Override
  public BearerTokenAuthentication readAuthenticationForRefreshToken(
      AuthRefreshToken refreshToken) {
    String principal =
        redisTemplate.boundValueOps(refresh_token_prefix + refreshToken.getTokenValue()).get();
    LoginUser user = null; // JSON.deserialize(principal, LoginUser.class);
    //    AuthToken accessToken = new AuthToken(TokenType.JWT,
    //            refreshToken.getTokenValue(),
    //            refreshToken.getIssuedAt(),
    //            refreshToken.getExpiresAt());
    AuthToken accessToken = null;
    assert user != null;
    return new BearerTokenAuthentication(user, accessToken, user.getAuthorities());
  }

  @Override
  public void removeRefreshToken(AuthRefreshToken token) {
    redisTemplate.delete(refresh_token_prefix + token.getTokenValue());
  }

  @Override
  public void removeAccessTokenUsingRefreshToken(AuthRefreshToken refreshToken) {}

  @Override
  public T getAccessToken(AuthenticationToken<String> authentication) {
    return this.readAccessToken(authentication.getToken());
  }

  @Override
  public Collection<AuthToken> findTokensByClientId(String clientId) {
    return null;
  }

  @SneakyThrows
  protected String buildStoreData(AuthToken token, Authentication authentication) {
    Collection<? extends GrantedAuthority> tempGrantedAuthority =
        ObjectUtil.defaultValue(authentication.getAuthorities(), Collections::emptyList);
    Set<String> authorities =
        tempGrantedAuthority.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    return mapper.writeValueAsString(
        TokenObject.builder()
            .authorities(authorities)
            .principalType(authentication.getPrincipal().getClass().getName())
            .principal(authentication.getPrincipal())
            .accessToken(token)
            .build());
  }

  @SneakyThrows
  protected TokenObject buildAuthToken(String data) {
    JsonNode node = mapper.readTree(data);
    JsonNode accessTokenNode = node.get("access_token");
    JsonNode principalNode = node.get("principal");
    JsonNode authoritiesNode = node.get("authorities");
    String principal_type = node.get("principal_type").asText();

    T authToken = mapper.treeToValue(accessTokenNode, authTokenClass);

    Class<? extends AuthenticatedPrincipal> principalType = ClassUtil.forName(principal_type);

    AuthenticatedPrincipal principal = mapper.treeToValue(principalNode, principalType);
    Set<String> authorities = mapper.convertValue(authoritiesNode, new TypeReference<>() {});

    return TokenObject.builder()
        .principal(principal)
        .principalType(principal_type)
        .authorities(authorities == null ? Collections.emptySet() : authorities)
        .accessToken(authToken)
        .build();
  }

  private AuthenticationToken<T> buildAuthenticationToken(String data, TokenObject accessToken) {
    DefaultAuthenticationDetails details = new DefaultAuthenticationDetails();
    details.setClientId(accessToken.getAccessToken().getClientId());
    return buildAuthenticationToken(data, accessToken, details);
  }

  protected AuthenticationToken<T> buildAuthenticationToken(
      String data, TokenObject tokenObject, AuthenticationDetails details) {

    AuthenticatedPrincipal principal = tokenObject.getPrincipal();
    List<GrantedAuthority> authorities =
        tokenObject.getAuthorities().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    BearerTokenAuthentication authentication =
        new BearerTokenAuthentication(principal, tokenObject.getAccessToken(), authorities);
    if (details != null) {
      authentication.setDetails(details);
    }
    return (AuthenticationToken<T>) authentication;
  }
}
