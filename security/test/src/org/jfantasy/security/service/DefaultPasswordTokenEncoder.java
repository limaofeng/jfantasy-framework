package org.jfantasy.security.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.framework.httpclient.HttpClientUtil;
import org.jfantasy.framework.httpclient.Response;
import org.jfantasy.framework.service.PasswordTokenEncoder;
import org.jfantasy.framework.service.PasswordTokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DefaultPasswordTokenEncoder implements PasswordTokenEncoder {

    private static final Log LOG = LogFactory.getLog(DefaultPasswordTokenEncoder.class);

    private final PasswordEncoder passwordEncoder;
    private final ApiGatewaySettings apiGatewaySettings;

    @Autowired
    public DefaultPasswordTokenEncoder(PasswordEncoder passwordEncoder, ApiGatewaySettings apiGatewaySettings) {
        this.passwordEncoder = passwordEncoder;
        this.apiGatewaySettings = apiGatewaySettings;
    }

    private boolean matches(String session, String encodedPassword) {
        try {
            Response response = HttpClientUtil.doGet(apiGatewaySettings.getUrl() + "/sms/configs/test/valid?sessionId=" + session + "&code=" + encodedPassword);
            return response.getStatusCode() < 400;
        } catch (IOException ignore) {
            LOG.error(ignore);
            return false;
        }
    }

    @Override
    public boolean matches(String operation, PasswordTokenType type, String username, String rawPassword, String encodedPassword) {
        if (type == PasswordTokenType.password) {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        } else if (type == PasswordTokenType.macode) {
            return matches(username + ":" + operation, encodedPassword);
        } else if (type == PasswordTokenType.token) {
            return true;
        }
        return false;
    }
}
