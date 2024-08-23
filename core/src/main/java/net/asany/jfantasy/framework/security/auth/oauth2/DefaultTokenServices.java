package net.asany.jfantasy.framework.security.auth.oauth2;

import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.autoconfigure.properties.SecurityProperties;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.TokenUsage;
import net.asany.jfantasy.framework.security.auth.core.*;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import net.asany.jfantasy.framework.security.auth.core.token.ConsumerTokenServices;
import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.auth.oauth2.jwt.JwtTokenService;
import net.asany.jfantasy.framework.security.auth.oauth2.jwt.JwtTokenServiceImpl;
import net.asany.jfantasy.framework.security.auth.oauth2.jwt.JwtUtils;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.AbstractAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.springframework.core.task.TaskExecutor;

/**
 * Token 服务
 *
 * @author limaofeng
 */
@Slf4j
public class DefaultTokenServices
    implements AuthorizationServerTokenServices<OAuth2AccessToken>,
        ResourceServerTokenServices<OAuth2AccessToken>,
        ConsumerTokenServices {

  @Setter private TokenStore<OAuth2AccessToken> tokenStore;
  @Setter private ClientDetailsService clientDetailsService;
  private final JwtTokenService jwtTokenService = new JwtTokenServiceImpl();
  private final TaskExecutor taskExecutor;
  private final SecurityProperties securityProperties;

  public DefaultTokenServices(
      SecurityProperties securityProperties,
      TokenStore<OAuth2AccessToken> tokenStore,
      ClientDetailsService clientDetailsService,
      TaskExecutor taskExecutor) {
    this.tokenStore = tokenStore;
    this.clientDetailsService = clientDetailsService;
    this.taskExecutor = taskExecutor;
    this.securityProperties = securityProperties;
  }

  @SneakyThrows
  @Override
  public OAuth2AccessToken createAccessToken(Authentication authentication) {
    LoginUser principal = authentication.getPrincipal();
    AuthenticationDetails details = authentication.getDetails();

    ClientDetails clientDetails =
        Optional.ofNullable(details.getClientDetails())
            .orElseGet(() -> this.clientDetailsService.loadClientByClientId(details.getClientId()));

    if (clientDetails == null) {
      throw new AuthenticationException("无效的 ClientID " + details.getClientId());
    }

    details.setClientDetails(clientDetails);

    AuthType authType = details.getAuthType();
    TokenUsage tokenUsage = details.getTokenUsage();

    ClientSecret clientSecret = details.getClientSecret();
    if (clientSecret == null && tokenUsage != null) {
      clientSecret = clientDetails.getClientSecret(tokenUsage.getClientSecretType()).orElse(null);
    }
    if (clientSecret == null && authType == AuthType.PASSWORD) {
      clientSecret = clientDetails.getClientSecret(ClientSecretType.ROTATING).orElse(null);
    }

    if (clientSecret == null) {
      throw new AuthenticationException("无效的 client_secret");
    }
    details.setClientSecret(clientSecret);

    Instant issuedAt = Instant.now();

    String secretValue = clientSecret.getSecretValue();
    int expires = clientSecret.getTokenExpires();
    Instant expiresAt = Instant.now().plus(expires, ChronoUnit.MINUTES);
    boolean supportRefreshToken = clientSecret.getType().supportsRefreshToken();

    if (details.getExpiresAt() != null) {
      expiresAt = details.getExpiresAt();
    }

    JwtTokenPayload.JwtTokenPayloadBuilder jwtTokenPayloadBuilder =
        JwtTokenPayload.builder()
            .kid(clientSecret.getId())
            .iss("https://www.asany.cn")
            .name(authentication.getName())
            .iat(issuedAt.getEpochSecond())
            .exp(expiresAt.getEpochSecond())
            .clientId(clientDetails.getClientId());

    if (principal != null) {
      jwtTokenPayloadBuilder.userId(principal.getUid());
    }

    JwtTokenPayload payload = jwtTokenPayloadBuilder.build();

    String tokenValue = generateTokenValue(payload, secretValue);

    OAuth2AccessToken accessToken =
        new OAuth2AccessToken(clientDetails.getClientId(), tokenValue, issuedAt, expiresAt);

    if (supportRefreshToken) {
      String refreshTokenValue = generateRefreshTokenValue();
      AuthRefreshToken refreshToken =
          new AuthRefreshToken(
              details.getClientId(),
              refreshTokenValue,
              issuedAt,
              issuedAt.plus(7, ChronoUnit.DAYS));
      tokenStore.storeRefreshToken(refreshToken, authentication);

      accessToken.setRefreshTokenValue(refreshTokenValue);
    }

    tokenStore.storeAccessToken(accessToken, authentication);

    return accessToken;
  }

  @Override
  public OAuth2AccessToken getAccessToken(Authentication authentication) {
    return null;
  }

  private void refreshAccessToken(OAuth2AccessToken accessToken, long expires) {
    accessToken.setExpiresAt(Instant.now().plus(expires, ChronoUnit.MINUTES));
    taskExecutor.execute(
        () ->
            this.tokenStore.storeAccessToken(
                accessToken, this.tokenStore.readAuthentication(accessToken.getTokenValue())));
  }

  private void refreshAccessToken(
      OAuth2AccessToken accessToken, long expires, BearerTokenAuthenticationToken authentication) {
    accessToken.setExpiresAt(Instant.now().plus(expires, ChronoUnit.MINUTES));
    taskExecutor.execute(
        () ->
            this.tokenStore.storeAccessToken(
                accessToken, this.tokenStore.readAuthentication(authentication)));
  }

  @Override
  public boolean revokeToken(String tokenValue) {
    OAuth2AccessToken accessToken = this.readAccessToken(tokenValue);

    if (accessToken == null) {
      return false;
    }

    String refreshTokenValue = accessToken.getRefreshTokenValue();

    if (refreshTokenValue != null) {
      AuthRefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
      this.tokenStore.removeRefreshToken(refreshToken);
    }

    this.tokenStore.removeAccessToken(accessToken);
    return true;
  }

  @Override
  public AuthenticationToken<OAuth2AccessToken> loadAuthentication(String tokenValue) {
    try {
      // 解析内容
      JwtTokenPayload payload = JwtUtils.payload(tokenValue);

      // 获取客户端配置
      ClientDetails clientDetails =
          clientDetailsService.loadClientByClientId(payload.getClientId());

      Optional<ClientSecret> clientSecretOptional = clientDetails.getClientSecret(payload.getKid());

      if (clientSecretOptional.isEmpty()) {
        throw new AuthenticationException("无效的 client_secret");
      }

      ClientSecret clientSecret = clientSecretOptional.get();

      int expires = clientSecret.getTokenExpires();

      // 验证 Token
      verifyToken(tokenValue, clientSecret.getSecretValue());

      // 获取令牌
      AuthenticationToken<OAuth2AccessToken> authentication =
          this.tokenStore.readAuthentication(tokenValue);

      if (authentication == null) {
        throw new InvalidTokenException("Token  " + tokenValue + " 不存在");
      }

      AuthenticationDetails details = authentication.getDetails();

      details.setClientSecret(clientSecret);
      details.setClientDetails(clientDetails);

      OAuth2AccessToken accessToken = authentication.getToken();

      // 如果续期方式为 Session 执行续期操作
      if (securityProperties.getAccessToken().isRefresh()
          && clientSecret.getType().isAutoRenewable()) {
        this.refreshAccessToken(accessToken, expires);
      }

      return authentication;
    } catch (Exception e) {
      log.warn("无效的 Token: {} Error: {}", tokenValue, e.getMessage());
      return null;
    }
  }

  @Override
  public AuthenticationToken<OAuth2AccessToken> loadAuthentication(
      AuthenticationToken<String> authenticationToken) {
    AuthenticationToken<OAuth2AccessToken> authentication =
        loadAuthentication(authenticationToken.getToken());
    if (authentication instanceof AbstractAuthenticationToken<OAuth2AccessToken> token) {
      token.setDetails(authenticationToken.getDetails().update(token.getDetails()));
    }
    return authentication;
  }

  @Override
  public OAuth2AccessToken readAccessToken(AuthenticationToken<String> authenticationToken) {
    return this.readAccessToken(authenticationToken.getToken());
  }

  @Override
  public OAuth2AccessToken readAccessToken(String tokenValue) {
    return this.loadAuthentication(tokenValue).getToken();
  }

  private void verifyToken(String accessToken, String secret) {
    try {
      jwtTokenService.verifyToken(accessToken, secret);
    } catch (ParseException | JOSEException e) {
      log.error("Token 验证失败：{}", e.getMessage());
      throw new InvalidTokenException("无效的 Token");
    }
  }

  @SneakyThrows
  private String generateTokenValue(JwtTokenPayload payload, String secret) {
    String tokenValue;
    do {
      tokenValue = jwtTokenService.generateToken(JSON.serialize(payload), secret);
    } while (tokenStore.readAccessToken(tokenValue) != null);
    return tokenValue;
  }

  private String generateRefreshTokenValue() {
    String tokenValue;
    do {
      tokenValue = StringUtil.generateNonceString(32);
    } while (tokenStore.readRefreshToken(tokenValue) != null);
    return tokenValue;
  }
}
