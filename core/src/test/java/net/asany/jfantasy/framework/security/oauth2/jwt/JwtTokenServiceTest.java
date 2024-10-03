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
package net.asany.jfantasy.framework.security.oauth2.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.AESEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.zip.DeflaterOutputStream;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.security.auth.oauth2.JwtTokenPayload;
import net.asany.jfantasy.framework.security.auth.oauth2.jwt.JwtTokenService;
import net.asany.jfantasy.framework.security.auth.oauth2.jwt.JwtTokenServiceImpl;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class JwtTokenServiceTest {

  private JwtTokenService jwtTokenService;

  @BeforeEach
  void setUp() {
    JSON.initialize();
    jwtTokenService = new JwtTokenServiceImpl();
  }

  @Test
  void generateToken() throws JOSEException {
    String client = StringUtil.generateNonceString("abcdef0123456789", 20);
    log.debug(" client = {}", client);
    String secret = StringUtil.generateNonceString("abcdef0123456789", 40);
    log.debug(" secret = {}", secret);
    JwtTokenPayload payload =
        JwtTokenPayload.builder()
            .userId(1L)
            .name("limaofeng")
            .email("limaofeng@msn.com")
            .exp(System.currentTimeMillis() / 1000 + 3600)
            .build();
    String token =
        jwtTokenService.generateToken(JWSAlgorithm.HS256, JOSEObjectType.JWT, payload, secret);
    log.debug(" token = {}", token);
  }

  @Test
  void verifyToken() throws JOSEException, ParseException {
    String payloadStr =
        "{\"uid\":1,\"token_type\":\"SESSION\",\"client_id\":\"6068485332c5fc853a65\"}";
    String secret = "3c833cf785c71234d5024aca9668dd466f050453";
    String token =
        jwtTokenService.generateToken(JWSAlgorithm.HS256, JOSEObjectType.JWT, payloadStr, secret);
    log.debug(" token = {}, token length: {}", token, token.length());

    JWSObject jwsObject = jwtTokenService.verifyToken(token, secret);
    assert jwsObject.getPayload().toString() != null;
    log.debug(" payloadStr = {}", payloadStr);
  }

  @Test
  void jwe() throws JOSEException, IOException {
    // Create a JWE header with the desired algorithm and compression method
    JWEHeader header =
        new JWEHeader.Builder(JWEAlgorithm.A256GCMKW, EncryptionMethod.A128GCM)
            .compressionAlgorithm(CompressionAlgorithm.DEF) // Set compression algorithm
            .build();

    // Create a JWE payload (claims)
    JSONObject claims = new JSONObject();
    claims.put("sub", "user123");
    claims.put("exp", System.currentTimeMillis() / 1000 + 3600); // Expiration time

    // Compress the payload data
    ByteArrayOutputStream compressedStream = new ByteArrayOutputStream();
    try (DeflaterOutputStream deflaterStream = new DeflaterOutputStream(compressedStream)) {
      deflaterStream.write(claims.toJSONString().getBytes());
    }

    // Create a JWE object with the header and compressed payload
    JWEObject jweObject = new JWEObject(header, new Payload(compressedStream.toByteArray()));

    // Create a JWK for encryption
    OctetSequenceKey jwk = new OctetSequenceKeyGenerator(256).generate();

    // Create an encrypter with the JWK
    JWEEncrypter encrypter = new AESEncrypter(jwk);

    // Encrypt the JWE object
    jweObject.encrypt(encrypter);

    // Serialize to compact form
    String jweString = jweObject.serialize();

    System.out.println("Compressed and encrypted JWE Token: " + jweString.length());
  }
}
