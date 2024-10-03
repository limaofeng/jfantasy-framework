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

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import java.text.ParseException;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.security.auth.oauth2.JwtTokenPayload;
import org.springframework.stereotype.Service;

/**
 * JwtToken 服务实现
 *
 * @author limaofeng
 */
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

  @Override
  public String generateToken(
      JWSAlgorithm alg, JOSEObjectType typ, JwtTokenPayload jwtPayload, String secret)
      throws JOSEException {
    return this.generateToken(alg, typ, JSON.serialize(jwtPayload), secret);
  }

  @Override
  public String generateToken(
      JWSAlgorithm alg, JOSEObjectType typ, String jwtPayload, String secret) throws JOSEException {
    // 创建JWS头，设置签名算法和类型
    JWSHeader jwsHeader =
        new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
    // 将负载信息封装到Payload中
    Payload payload = new Payload(jwtPayload);
    // 创建JWS对象
    JWSObject jwsObject = new JWSObject(jwsHeader, payload);
    // 创建HMAC签名器
    JWSSigner jwsSigner = new MACSigner(secret);
    // 签名
    jwsObject.sign(jwsSigner);
    return jwsObject.serialize();
  }

  @Override
  public JWSObject verifyToken(String token, String secret) throws ParseException, JOSEException {
    // 从token中解析JWS对象
    JWSObject jwsObject = JWSObject.parse(token);
    // 创建HMAC验证器
    JWSVerifier jwsVerifier = new MACVerifier(secret);
    if (!jwsObject.verify(jwsVerifier)) {
      throw new JwtInvalidException("token签名不合法！");
    }
    return jwsObject;
  }
}
