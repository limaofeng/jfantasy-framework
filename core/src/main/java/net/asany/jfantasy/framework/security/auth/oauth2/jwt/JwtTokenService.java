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
