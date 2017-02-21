package org.jfantasy.trade.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

public class PasswordEncoderTest {

    private static Log LOG = LogFactory.getLog(PasswordEncoderTest.class);

    private PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    @Test
    public void password(){
        String encodePassword = passwordEncoder.encode("123456");
        LOG.debug("encode = " + encodePassword);

        Assert.assertTrue(passwordEncoder.matches("123456",encodePassword));
    }

}
