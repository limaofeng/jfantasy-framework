package org.jfantasy.auth.rest;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.auth.rest.models.LoginForm;
import org.jfantasy.auth.rest.models.Scope;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.member.bean.Member;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class LoginTest {

    private static final Log LOG = LogFactory.getLog(LoginTest.class);

    @Test
    public void login(){

        LoginForm loginForm = new LoginForm();

        loginForm.setScope(Scope.member);
        loginForm.setUsername("mayanfei18");
        loginForm.setPassword("123456");
        loginForm.setUserType("doctor");

        String baseUrl = "http://114.55.142.155:8081";

        while (true) {
            ResponseEntity<Member> responseEntity = RESTful.restTemplate.postForEntity(baseUrl + "/login", loginForm, Member.class);

            if (responseEntity.hasBody()) {
                if (responseEntity.getBody().getCode() == null) {
                    LOG.error(responseEntity.getBody());
                }
            }
        }

    }

}
