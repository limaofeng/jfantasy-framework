package org.jfantasy.auth.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.auth.rest.models.LoginForm;
import org.jfantasy.auth.rest.models.Scope;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.ThreadJacksonMixInHolder;
import org.jfantasy.member.bean.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OauthApiServerApplication.class)
@ActiveProfiles("dev")
public class AuthControllerTest {

    private static final Log LOG = LogFactory.getLog(AuthControllerTest.class);

    @Autowired
    private AuthController authController;

    @Test
    @Transactional
    public void login() throws Exception {
        LoginForm loginForm = new LoginForm();

        loginForm.setScope(Scope.member);
        loginForm.setUsername("mayanfei18");
        loginForm.setPassword("123456");
        loginForm.setUserType("doctor");



        while (true) {
            ThreadJacksonMixInHolder.getMixInHolder().addIgnorePropertyNames(Member.class,Member.BASE_JSONFIELDS);
            Object result = this.authController.login(loginForm);
            String json = JSON.serialize(result);
            LOG.debug(json);

            if (json.indexOf("code") == -1) {
                LOG.error(json);
            }
            Thread.sleep(2000);
        }
    }

}