package net.asany.jfantasy.framework.security.auth.oauth2.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import java.text.ParseException;
import net.asany.jfantasy.framework.security.auth.oauth2.JwtTokenPayload;

/**
 * JwtToken 服务
 *
 * @author limaofeng
 */
public interface JwtTokenService {

  /**
   * 生成 Token
   *
   * @param payload 内容
   * @param secret 密钥
   * @return Token
   * @throws JOSEException 异常
   */
  String generateToken(JWSAlgorithm alg, JOSEObjectType typ, JwtTokenPayload payload, String secret)
      throws JOSEException;

  /**
   * 生成 Token
   *
   * @param payload 内容
   * @param secret 密钥
   * @return Token
   * @throws JOSEException 异常
   */
  String generateToken(JWSAlgorithm alg, JOSEObjectType typ, String payload, String secret)
      throws JOSEException;

  /**
   * 验证 Token
   *
   * @param token 令牌
   * @param secret 密钥
   * @return 返回内容
   * @throws ParseException 解析异常
   * @throws JOSEException 解析异常
   */
  JWSObject verifyToken(String token, String secret) throws ParseException, JOSEException;
}
