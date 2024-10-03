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
package net.asany.jfantasy.framework.security.auth.oauth2.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.TokenUsage;
import net.asany.jfantasy.framework.security.auth.core.AuthorizationGrantType;
import net.asany.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.springframework.http.HttpHeaders;

/**
 * OAuth2 认证详情
 *
 * @author limaofeng
 */
@Getter
@EqualsAndHashCode(callSuper = false)
public class OAuth2AuthenticationDetails extends WebAuthenticationDetails {

  /**
   * 客户端的ID<br>
   * 分配的 apiKey
   */
  @JsonProperty("client_id")
  private String clientId;

  @JsonIgnore private String clientSecretStr;

  /** 授权模式 */
  @JsonProperty("grant_type")
  private AuthorizationGrantType grantType;

  /**
   * 授权码<br>
   * 授权模式 为 授权码模式 时 必填
   */
  private String code;

  /**
   * 用户名<br>
   * 授权模式 为 密码模式 时 必填
   */
  private String username;

  /**
   * 密码<br>
   * 授权模式 为 密码模式 时 必填
   */
  private String password;

  /** 表示权限范围，可选项。 */
  private Set<String> scopes;

  /**
   * 更新令牌<br>
   * 授权模式 为 更新令牌模式 时 必填
   */
  @JsonProperty("refresh_token")
  private String refreshToken;

  public OAuth2AuthenticationDetails() {
    this.authType = AuthType.OAUTH2;
  }

  public OAuth2AuthenticationDetails(HttpServletRequest request) {
    super(request);
    this.authType = AuthType.OAUTH2;
    this.tokenUsage = TokenUsage.OAUTH;
    String grant_type = request.getParameter("grant_type");
    if (StringUtil.isNotBlank(grant_type)) {
      this.grantType = AuthorizationGrantType.valueOf(grant_type);
    }
    this.clientId = extractClientId(request);
    if (this.grantType == AuthorizationGrantType.PASSWORD) {
      this.username = request.getParameter("username");
      this.password = request.getParameter("password");

    } else if (this.grantType == AuthorizationGrantType.CLIENT_CREDENTIALS) {
      this.clientSecretStr = extractClientSecret(request);
    } else if (this.grantType == AuthorizationGrantType.AUTHORIZATION_CODE) {
      this.code = request.getParameter("code");
    }
  }

  public static OAuth2AuthenticationDetails password(
      String clientId, String username, String password, String... scopes) {
    OAuth2AuthenticationDetails details = new OAuth2AuthenticationDetails();
    details.grantType = AuthorizationGrantType.PASSWORD;
    details.clientId = clientId;
    details.username = username;
    details.password = password;
    details.tokenUsage = TokenUsage.OAUTH;
    details.scopes = Set.of(scopes);
    return details;
  }

  public static OAuth2AuthenticationDetails clientCredentials(
      String clientId, String clientSecret, String... scopes) {
    OAuth2AuthenticationDetails details = new OAuth2AuthenticationDetails();
    details.grantType = AuthorizationGrantType.CLIENT_CREDENTIALS;
    details.clientId = clientId;
    details.clientSecretStr = clientSecret;
    details.tokenUsage = TokenUsage.OAUTH;
    details.scopes = Set.of(scopes);
    return details;
  }

  public static OAuth2AuthenticationDetails authorizationCode(
      String clientId, String code, String... scopes) {
    OAuth2AuthenticationDetails details = new OAuth2AuthenticationDetails();
    details.grantType = AuthorizationGrantType.AUTHORIZATION_CODE;
    details.clientId = clientId;
    details.code = code;
    details.tokenUsage = TokenUsage.OAUTH;
    details.scopes = Set.of(scopes);
    return details;
  }

  /**
   * 从 HttpServletRequest 中提取 client_id
   *
   * @param request HttpServletRequest 对象
   * @return client_id 或 null 如果未找到
   */
  public static String extractClientId(HttpServletRequest request) {
    // 尝试从请求参数中获取 client_id
    String clientId = request.getParameter("client_id");
    // 如果请求参数中未找到，则从 Authorization 头中获取
    if (clientId == null) {
      String[] clientCredentials = extractClientCredentialsFromHeader(request);
      clientId = (clientCredentials != null) ? clientCredentials[0] : null;
    }

    return clientId;
  }

  /**
   * 从 HttpServletRequest 中提取 client_secret
   *
   * @param request HttpServletRequest 对象
   * @return client_secret 或 null 如果未找到
   */
  public static String extractClientSecret(HttpServletRequest request) {
    // 尝试从请求参数中获取 client_secret
    String clientSecret = request.getParameter("client_secret");

    // 如果请求参数中未找到，则从 Authorization 头中获取
    if (clientSecret == null) {
      String[] clientCredentials = extractClientCredentialsFromHeader(request);
      clientSecret = (clientCredentials != null) ? clientCredentials[1] : null;
    }

    return clientSecret;
  }

  /**
   * 从 Authorization 头中提取客户端凭据
   *
   * @param request HttpServletRequest 对象
   * @return 包含 client_id 和 client_secret 的数组，或者 null 如果未找到
   */
  private static String[] extractClientCredentialsFromHeader(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
      // 从 Authorization 头中提取 Base64 编码的客户端凭据
      String base64Credentials = authorizationHeader.substring("Basic ".length()).trim();
      byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
      String credentials = new String(decodedCredentials);

      // 客户端凭据格式为 "client_id:client_secret"
      String[] clientCredentials = credentials.split(":", 2);
      if (clientCredentials.length == 2) {
        return clientCredentials;
      }
    }
    return null;
  }
}
