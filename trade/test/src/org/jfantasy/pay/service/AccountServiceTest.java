package org.jfantasy.pay.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

/**
 * 账户测试
 */
public class AccountServiceTest {

    private static final Log LOG = LogFactory.getLog(AccountServiceTest.class);

    private PasswordEncoder passwordEncoder =  new StandardPasswordEncoder();

    @Test
    public void encode(){
        String password = passwordEncoder.encode("123456");

        LOG.debug(password);

        LOG.debug(passwordEncoder.matches("123456",password));
    }

    @Test
    public void substring(){
        LOG.debug("20120710".substring(0,6));

    }

}