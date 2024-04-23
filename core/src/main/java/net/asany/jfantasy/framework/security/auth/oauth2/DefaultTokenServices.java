package net.asany.jfantasy.framework.security.auth.oauth2;

import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.auth.core.*;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import net.asany.jfantasy.framework.security.auth.core.token.ConsumerTokenServices;
import net.asany.jfantasy.framework.security.auth.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.auth.oauth2.core.*;
import net.asany.jfantasy.framework.security.auth.oauth2.jwt.JwtTokenService;
import net.asany.jfantasy.framework.security.auth.oauth2.jwt.JwtTokenServiceImpl;
import net.asany.jfantasy.framework.security.auth.oauth2.jwt.JwtUtils;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;
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

  public DefaultTokenServices(
      TokenStore<OAuth2AccessToken> tokenStore,
      ClientDetailsService clientDetailsService,
      TaskExecutor taskExecutor) {
    this.tokenStore = tokenStore;
    this.clientDetailsService = clientDetailsService;
    this.taskExecutor = taskExecutor;
  }

  @SneakyThrows
  @Override
  public OAuth2AccessToken createAccessToken(AuthenticationToken authentication) {
    LoginUser principal = authentication.getPrincipal();
    OAuth2AuthenticationDetails details = authentication.getDetails();
    ClientDetails clientDetails =
        this.clientDetailsService.loadClientByClientId(details.getClientId());

    TokenType tokenType = details.getTokenType();

    int expires = clientDetails.getTokenExpires(TokenType.PERSONAL_ACCESS_TOKEN);

    boolean supportRefreshToken = false;
    Instant issuedAt = Instant.now();
    Instant expiresAt = null;

    String secret = null;

    if (tokenType == TokenType.PERSONAL_ACCESS_TOKEN) {
      secret = clientDetails.getClientSecret(ClientSecretType.PERSONAL_ACCESS_TOKEN);
      expiresAt = details.getExpiresAt();
    } else if (tokenType == TokenType.JWT) {
      if (details.getGrantType() == AuthorizationGrantType.CLIENT_CREDENTIALS) {
        if (clientDetails.getClientSecrets(ClientSecretType.OAUTH).stream()
            .noneMatch(s -> s.equals(details.getClientSecret()))) {
          throw new AuthenticationException("无效的 client_secret");
        }
        secret = details.getClientSecret();
        expiresAt = details.getExpiresAt();
      } else {
        supportRefreshToken = true;
        secret = clientDetails.getClientSecret(ClientSecretType.OAUTH);
        expiresAt = Instant.now().plus(expires, ChronoUnit.MINUTES);
      }
    } else if (tokenType == TokenType.SESSION_ID) {
      secret = clientDetails.getClientSecret(ClientSecretType.SESSION);
      expiresAt = Instant.now().plus(expires, ChronoUnit.MINUTES);
    }

    if (secret == null) {
      throw new AuthenticationException("无效的 client_secret");
    }

    details.setClientSecret(secret);

    JwtTokenPayload.JwtTokenPayloadBuilder jwtTokenPayloadBuilder =
        JwtTokenPayload.builder()
            .iss("https://www.asany.cn")
            .name(authentication.getName())
            .clientId(clientDetails.getClientId())
            //            .tokenType(tokenType)
            .iat(issuedAt.getEpochSecond())
            .exp(expiresAt.getEpochSecond());

    if (principal != null) {
      jwtTokenPayloadBuilder.userId(principal.getUid());
    }

    JwtTokenPayload payload = jwtTokenPayloadBuilder.build();

    String tokenValue = generateTokenValue(payload, secret);

    OAuth2AccessToken accessToken = new OAuth2AccessToken("", tokenValue, issuedAt, expiresAt);

    if (supportRefreshToken) {
      String refreshTokenValue = generateRefreshTokenValue();
      AuthRefreshToken refreshToken =
          new AuthRefreshToken("", refreshTokenValue, issuedAt, expiresAt.plus(7, ChronoUnit.DAYS));
      tokenStore.storeRefreshToken(refreshToken, authentication);

      accessToken.setRefreshTokenValue(refreshTokenValue);
    }

    tokenStore.storeAccessToken(accessToken, authentication);

    return accessToken;
  }

  @Override
  public OAuth2AccessToken getAccessToken(AuthenticationToken authentication) {
    return null;
  }

  private void refreshAccessToken(OAuth2AccessToken accessToken, long expires) {
    accessToken.setExpiresAt(accessToken.getExpiresAt().plus(expires, ChronoUnit.MINUTES));
    taskExecutor.execute(
        () ->
            this.tokenStore.storeAccessToken(
                accessToken, this.tokenStore.readAuthentication(accessToken.getTokenValue())));
  }

  private void refreshAccessToken(
      OAuth2AccessToken accessToken, long expires, BearerTokenAuthenticationToken authentication) {
    accessToken.setExpiresAt(accessToken.getExpiresAt().plus(expires, ChronoUnit.MINUTES));
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
  public BearerTokenAuthentication loadAuthentication(BearerTokenAuthenticationToken accessToken) {
    AuthToken token = this.readAccessToken(accessToken);
    if (token == null) {
      return null;
    }
    return this.tokenStore.readAuthentication(token.getTokenValue());
  }

  @Override
  public BearerTokenAuthentication loadAuthentication(String accessToken) {
    AuthToken token = this.readAccessToken(accessToken);
    if (token == null) {
      return null;
    }
    return this.tokenStore.readAuthentication(token.getTokenValue());
  }

  @Override
  public OAuth2AccessToken readAccessToken(String tokenValue) {
    try {
      // 解析内容
      JwtTokenPayload payload = JwtUtils.payload(tokenValue);

      // 获取客户端配置
      ClientDetails clientDetails =
          clientDetailsService.loadClientByClientId(payload.getClientId());

      Set<String> secrets = clientDetails.getClientSecrets(ClientSecretType.OAUTH);
      int expires = clientDetails.getTokenExpires(TokenType.PERSONAL_ACCESS_TOKEN);

      // 验证 Token
      verifyToken(tokenValue, secrets);

      // 获取令牌
      OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(tokenValue);

      if (accessToken == null) {
        throw new InvalidTokenException("无效的 Token");
      }

      // 如果续期方式为 Session 执行续期操作
      if (accessToken.getTokenType() == TokenType.SESSION_ID) {
        this.refreshAccessToken(accessToken, expires);
      }

      return accessToken;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public OAuth2AccessToken readAccessToken(BearerTokenAuthenticationToken token) {
    try {
      TokenType tokenType = token.getTokenType();

      // 解析内容
      JwtTokenPayload payload = JwtUtils.payload(token.getToken());

      // 获取客户端配置
      ClientDetails clientDetails =
          clientDetailsService.loadClientByClientId(payload.getClientId());

      ClientSecretType clientSecretType = tokenType == TokenType.PERSONAL_ACCESS_TOKEN
        ? ClientSecretType.PERSONAL_ACCESS_TOKEN
        : ClientSecretType.OAUTH;

      Set<String> secrets =
          clientDetails.getClientSecrets(clientSecretType);
      int expires = clientDetails.getTokenExpires(tokenType);

      // 验证 Token
      verifyToken(token.getToken(), secrets);

      // 获取令牌
      OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(token.getToken());

      if (accessToken == null) {
        throw new InvalidTokenException("无效的 Token");
      }

      // 如果续期方式为 Session 执行续期操作
      if (tokenType == TokenType.SESSION_ID) {
        this.refreshAccessToken(accessToken, expires, token);
      }

      return accessToken;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  private void verifyToken(String accessToken, Set<String> secrets) throws Exception {
    Exception firstException = null;
    for (String secret : secrets) {
      try {
        jwtTokenService.verifyToken(accessToken, secret);
        return;
      } catch (ParseException | JOSEException e) {
        firstException = firstException == null ? e : firstException;
      }
    }
    if (firstException != null) {
      throw firstException;
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
