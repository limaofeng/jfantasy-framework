package org.jfantasy.framework.security.oauth2.jwt;

import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface JwtTokenService {

    /**
     * 生成 Token
     * @param payloadStr
     * @param secret
     * @return
     * @throws JOSEException
     */
    String generateToken(String payloadStr, String secret) throws JOSEException;

    /**
     * 验证 Token
     * @param token
     * @param secret
     * @return
     * @throws ParseException
     * @throws JOSEException
     */
    String verifyToken(String token, String secret) throws ParseException, JOSEException;

}
