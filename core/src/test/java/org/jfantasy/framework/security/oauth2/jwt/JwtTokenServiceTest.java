package org.jfantasy.framework.security.oauth2.jwt;

import com.nimbusds.jose.JOSEException;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.StringUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class JwtTokenServiceTest {

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenServiceImpl();
    }

    @Test
    void generateToken() throws JOSEException {
        String client = StringUtil.generateNonceString("abcdef0123456789", 20);
        log.debug(" client = " + client);
        String secret = StringUtil.generateNonceString("abcdef0123456789", 40);
        log.debug(" secret = " + secret);
        String token = jwtTokenService.generateToken("我是 ASANY", secret);
        log.debug(" token = " + token);
    }

    @Test
    void verifyToken() {

    }
}