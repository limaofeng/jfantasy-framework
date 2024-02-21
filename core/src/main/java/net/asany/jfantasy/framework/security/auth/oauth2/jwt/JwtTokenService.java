package net.asany.jfantasy.framework.security.auth.oauth2.jwt;

import com.nimbusds.jose.JOSEException;
import java.text.ParseException;

/**
 * JwtToken 服务
 *
 * @author limaofeng
 */
public interface JwtTokenService {

  /**
   * 生成 Token
   *
   * @param payloadStr 内容
   * @param secret 密钥
   * @return Token
   * @throws JOSEException 异常
   */
  String generateToken(String payloadStr, String secret) throws JOSEException;

  /**
   * 验证 Token
   *
   * @param token 令牌
   * @param secret 密钥
   * @return 返回内容
   * @throws ParseException 解析异常
   * @throws JOSEException 解析异常
   */
  String verifyToken(String token, String secret) throws ParseException, JOSEException;
}
