package net.asany.jfantasy.framework.security.auth;

import java.util.regex.Pattern;
import lombok.Getter;

/**
 * 令牌类型
 *
 * <p>令牌类型是一个枚举，它定义了所有支持的令牌类型。
 */
@Getter
public enum AuthType {
  /**
   * 基本认证
   *
   * <p>BASIC 是一种简单的认证机制，它使用用户名和密码进行身份 在这种模式下，客户端将用，名和密码编码为 Base64 符串 求头的 Authorization 字段中。
   */
  BASIC,
  /**
   * BEARER
   *
   * <p>Bearer 是一种令牌传递机制，通常在 HTTP 请求头的 Authorization 字段中使用。 Bearer 令牌可以是多种格式，包括但不限于 JSON Web Token
   * (JWT)。
   */
  BEARER,
  /**
   * API 密钥
   *
   * <p>API 密钥是一种无状态的令牌，它不包含用户的身份信息，只包含了一些其他的元数据。
   */
  API_KEY,
  /**
   * 密码认证
   *
   * <p>密码认证使用用户名和密码进行身份验证，通常适用于传统的登录系统。在这种模式下，用户直接向客户端提供凭证，客户端通过这些凭证向服务器请求访问令牌。
   */
  PASSWORD,
  /**
   * OAuth2
   *
   * <p>OAuth2 是一种开放标准，它定义了一种授权框架，允许第三方应用访问用户的资源。 OAuth2 有多种授权模式，包括但不限于授权码模式、密码模式、客户端模式和隐式模式
   */
  OAUTH2;

  public static AuthType of(String token) {
    if (token.startsWith("ak-")) {
      return API_KEY;
    }
    if (mightBeJwt(token)) {
      return BEARER;
    }
    throw new IllegalArgumentException("Unknown token type");
  }

  private static boolean mightBeJwt(String token) {
    // 检查 token 是否包含两个点（`.`），这是 JWT 结构的基本要求
    if (token.split("\\.").length == 3) {
      String[] parts = token.split("\\.");
      // 检查每一部分是否为有效的 Base64 编码
      return isBase64(parts[0]) && isBase64(parts[1]);
    }
    return false;
  }

  private static boolean isBase64(String string) {
    // 用于检测字符串是否为 Base64 编码的正则表达式
    String base64Regex = "^[A-Za-z0-9+/]+={0,2}$";
    // 移除 JWT 中的 padding（等号），因为 Base64 编码可能不包含它们
    String noPadding = string.replaceAll("=", "");
    // 检查字符串是否匹配 Base64 编码的模式
    return Pattern.matches(base64Regex, noPadding);
  }
}
