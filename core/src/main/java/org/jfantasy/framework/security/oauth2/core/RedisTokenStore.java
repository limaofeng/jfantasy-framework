package org.jfantasy.framework.security.oauth2.core;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import org.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Redis 令牌存储器
 *
 * @author limaofeng
 */
@ConditionalOnClass(StringRedisTemplate.class)
@ConditionalOnMissingBean(TokenStore.class)
public class RedisTokenStore implements TokenStore, InitializingBean {

    private final String ASSESS_TOKEN_PREFIX = "assess_token:";
    private final String ASSESS_TOKEN_PRINCIPAL_PREFIX = "assess_token:principal:";
    private final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private final String REFRESH_TOKEN_PRINCIPAL_PREFIX = "refresh_token:principal:";
    private final String AUTHORIZATION_CODE_PREFIX = "authorization_code:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ValueOperations valueOperations;
    private ListOperations listOperations;

    @Override
    public void afterPropertiesSet() throws Exception {
        valueOperations = redisTemplate.opsForValue();
        listOperations = redisTemplate.opsForList();
    }

    @Override
    public BearerTokenAuthentication readAuthentication(BearerTokenAuthenticationToken token) {
        return null;
    }

    @Override
    public BearerTokenAuthentication readAuthentication(String token) {
        return null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String principal = principalToString(authentication);

        String key = token.getTokenValue();

        valueOperations.set(ASSESS_TOKEN_PREFIX + key, token);
        valueOperations.set(ASSESS_TOKEN_PRINCIPAL_PREFIX + key, principal);

        long expire = Duration.between(token.getExpiresAt(), token.getIssuedAt()).toMinutes();
        redisTemplate.expire(ASSESS_TOKEN_PREFIX + key, expire, TimeUnit.MINUTES);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return null;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {

    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        String principal = principalToString(authentication);

        String key = refreshToken.getTokenValue();

        valueOperations.set(REFRESH_TOKEN_PREFIX + key, principal);

        listOperations.leftPush(REFRESH_TOKEN_PREFIX + key, principal);

        long expire = Duration.between(refreshToken.getExpiresAt(), refreshToken.getIssuedAt()).toMinutes();
        redisTemplate.expire(REFRESH_TOKEN_PREFIX + key, expire, TimeUnit.MINUTES);
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        String token = redisTemplate.boundValueOps(REFRESH_TOKEN_PREFIX + tokenValue).get();
        if (StringUtils.isEmpty(token)) {

        }
        return null;
    }

    @Override
    public BearerTokenAuthentication readAuthenticationForRefreshToken(OAuth2RefreshToken refreshToken) {
        String principal = redisTemplate.boundValueOps(REFRESH_TOKEN_PRINCIPAL_PREFIX + refreshToken.getTokenValue()).get();

        LoginUser user = JSON.deserialize(principal, LoginUser.class);

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, refreshToken.getTokenValue(), refreshToken.getIssuedAt(), refreshToken.getExpiresAt());
        return new BearerTokenAuthentication(user, accessToken, user.getAuthorities());
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        redisTemplate.delete(REFRESH_TOKEN_PRINCIPAL_PREFIX + token.getTokenValue());
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {

    }

    @Override
    public OAuth2AccessToken getAccessToken(BearerTokenAuthentication authentication) {
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return null;
    }

    private String principalToString(Authentication authentication) {
        return JSON.serialize(authentication.getPrincipal());
    }
}
