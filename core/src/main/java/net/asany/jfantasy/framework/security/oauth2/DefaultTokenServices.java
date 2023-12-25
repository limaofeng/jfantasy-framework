package net.asany.jfantasy.framework.security.oauth2;

import com.nimbusds.jose.JOSEException;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.oauth2.core.*;
import net.asany.jfantasy.framework.security.oauth2.core.token.AuthorizationServerTokenServices;
import net.asany.jfantasy.framework.security.oauth2.core.token.ConsumerTokenServices;
import net.asany.jfantasy.framework.security.oauth2.core.token.ResourceServerTokenServices;
import net.asany.jfantasy.framework.security.oauth2.jwt.JwtTokenService;
import net.asany.jfantasy.framework.security.oauth2.jwt.JwtTokenServiceImpl;
import net.asany.jfantasy.framework.security.oauth2.jwt.JwtUtils;
import net.asany.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.springframework.core.task.TaskExecutor;

/**
 * Token 服务
 *
 * @author limaofeng
 */
@Slf4j
public class DefaultTokenServices
    implements AuthorizationServerTokenServices,
        ResourceServerTokenServices,
        ConsumerTokenServices {

  private TokenStore tokenStore;
  private ClientDetailsService clientDetailsService;
  private final JwtTokenService jwtTokenService = new JwtTokenServiceImpl();
  private final TaskExecutor taskExecutor;

  public DefaultTokenServices(
      TokenStore tokenStore, ClientDetailsService clientDetailsService, TaskExecutor taskExecutor) {
    this.tokenStore = tokenStore;
    this.clientDetailsService = clientDetailsService;
    this.taskExecutor = taskExecutor;
  }

  @SneakyThrows
  @Override
  public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) {
    LoginUser principal = (LoginUser) authentication.getPrincipal();
    OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
    ClientDetails clientDetails =
        this.clientDetailsService.loadClientByClientId(details.getClientId());

    TokenType tokenType = details.getTokenType();

    int expires = clientDetails.getTokenExpires();

    boolean supportRefreshToken = false;
    Instant issuedAt = Instant.now();
    Instant expiresAt = null;

    String secret = null;

    if (tokenType == TokenType.PERSONAL) {
      secret = clientDetails.getClientSecret(tokenType.getClientSecretType());
      expiresAt = details.getExpiresAt();
    } else if (tokenType == TokenType.TOKEN) {
      if (details.getGrantType() == AuthorizationGrantType.CLIENT_CREDENTIALS) {
        if (clientDetails.getClientSecrets(ClientSecretType.OAUTH).stream()
            .noneMatch(s -> s.equals(details.getClientSecret()))) {
          throw new AuthenticationException("无效的 client_secret");
        }
        secret = details.getClientSecret();
        expiresAt = details.getExpiresAt();
      } else {
        supportRefreshToken = true;
        secret = clientDetails.getClientSecret(tokenType.getClientSecretType());
        expiresAt = Instant.now().plus(expires, ChronoUnit.MINUTES);
      }
    } else if (tokenType == TokenType.SESSION) {
      secret = clientDetails.getClientSecret(tokenType.getClientSecretType());
      expiresAt = Instant.now().plus(expires, ChronoUnit.MINUTES);
    }

    if (secret == null) {
      throw new AuthenticationException("无效的 client_secret");
    }

    details.setClientSecret(secret);

    JwtTokenPayload.JwtTokenPayloadBuilder jwtTokenPayloadBuilder =
        JwtTokenPayload.builder()
            .name(authentication.getName())
            .clientId(clientDetails.getClientId())
            .tokenType(tokenType)
            .expiresAt(expiresAt);

    if (principal != null) {
      jwtTokenPayloadBuilder.uid(principal.getUid());
    }

    JwtTokenPayload payload = jwtTokenPayloadBuilder.build();

    String tokenValue = generateTokenValue(payload, secret);

    OAuth2AccessToken accessToken =
        new OAuth2AccessToken(tokenType, tokenValue, issuedAt, expiresAt);

    if (supportRefreshToken) {
      String refreshTokenValue = generateRefreshTokenValue();
      OAuth2RefreshToken refreshToken =
          new OAuth2RefreshToken(refreshTokenValue, issuedAt, expiresAt.plus(7, ChronoUnit.DAYS));
      tokenStore.storeRefreshToken(refreshToken, authentication);

      accessToken.setRefreshTokenValue(refreshTokenValue);
    }

    tokenStore.storeAccessToken(accessToken, authentication);

    return accessToken;
  }

  @Override
  public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
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
      OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
      this.tokenStore.removeRefreshToken(refreshToken);
    }

    this.tokenStore.removeAccessToken(accessToken);
    return true;
  }

  @Override
  public BearerTokenAuthentication loadAuthentication(BearerTokenAuthenticationToken accessToken) {
    OAuth2AccessToken token = this.readAccessToken(accessToken);
    if (token == null) {
      return null;
    }
    return this.tokenStore.readAuthentication(token.getTokenValue());
  }

  @Override
  public BearerTokenAuthentication loadAuthentication(String accessToken) {
    OAuth2AccessToken token = this.readAccessToken(accessToken);
    if (token == null) {
      return null;
    }
    return this.tokenStore.readAuthentication(token.getTokenValue());
  }

  @Override
  public OAuth2AccessToken readAccessToken(String accessToken) {
    try {
      // 解析内容
      JwtTokenPayload payload = JwtUtils.payload(accessToken);

      // 获取客户端配置
      ClientDetails clientDetails =
          clientDetailsService.loadClientByClientId(payload.getClientId());

      Set<String> secrets =
          clientDetails.getClientSecrets(payload.getTokenType().getClientSecretType());
      int expires = clientDetails.getTokenExpires();

      // 验证 Token
      verifyToken(accessToken, secrets);

      // 获取令牌
      OAuth2AccessToken oAuth2AccessToken = this.tokenStore.readAccessToken(accessToken);

      if (oAuth2AccessToken == null) {
        throw new InvalidTokenException("无效的 Token");
      }

      // 如果续期方式为 Session 执行续期操作
      if (payload.getTokenType() == TokenType.SESSION) {
        this.refreshAccessToken(oAuth2AccessToken, expires);
      }

      return oAuth2AccessToken;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  @Override
  public OAuth2AccessToken readAccessToken(BearerTokenAuthenticationToken accessToken) {
    try {
      // 解析内容
      JwtTokenPayload payload = JwtUtils.payload(accessToken.getToken());

      // 获取客户端配置
      ClientDetails clientDetails =
          clientDetailsService.loadClientByClientId(payload.getClientId());
      Set<String> secrets =
          clientDetails.getClientSecrets(payload.getTokenType().getClientSecretType());
      int expires = clientDetails.getTokenExpires();

      // 验证 Token
      verifyToken(accessToken.getToken(), secrets);

      // 获取令牌
      OAuth2AccessToken oAuth2AccessToken = this.tokenStore.readAccessToken(accessToken.getToken());

      if (oAuth2AccessToken == null) {
        throw new InvalidTokenException("无效的 Token");
      }

      // 如果续期方式为 Session 执行续期操作
      if (payload.getTokenType() == TokenType.SESSION) {
        this.refreshAccessToken(oAuth2AccessToken, expires, accessToken);
      }

      return oAuth2AccessToken;
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

  public void setTokenStore(TokenStore tokenStore) {
    this.tokenStore = tokenStore;
  }

  public void setClientDetailsService(ClientDetailsService clientDetailsService) {
    this.clientDetailsService = clientDetailsService;
  }

  @SneakyThrows
  private String generateTokenValue(JwtTokenPayload payload, String secret) {
    String tokenValue;
    do {
      payload.setNonce(StringUtil.generateNonceString(32));
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
