package net.asany.jfantasy.framework.security.auth;

import java.util.regex.Pattern;
import lombok.Getter;

/**
 * 令牌类型
 *
 * <p>令牌类型是一个枚举，它定义了所有支持的令牌类型。
 */
@Getter
public enum TokenType {
  /**
   * JWT 令牌
   *
   * <p>JWT 令牌是一种无状态的令牌，它包含了用户的身份信息，以及一些其他的元数据。
   */
  JWT,
  /**
   * API 密钥
   *
   * <p>API 密钥是一种无状态的令牌，它不包含用户的身份信息，只包含了一些其他的元数据。
   */
  API_KEY,
  /**
   * 会话令牌
   *
   * <p>会话令牌是一种有状态的令牌，它包含了用户的身份信息，以及一些其他的元数据。
   */
  SESSION_ID,
  /**
   * Access Token
   *
   * <p>Access Token 是一种有状态的令牌，它包含了用户的身份信息，以及一些其他的元数据。
   */
  PERSONAL_ACCESS_TOKEN;

  public static TokenType of(String token) {
    if (token.startsWith("ak-")) {
      return API_KEY;
    }
    if (mightBeJwt(token)) {
      return JWT;
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
