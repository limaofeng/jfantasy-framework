package org.jfantasy.member.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.trade.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private static final Log LOG = LogFactory.getLog(AccountService.class);

    private final ApiGatewaySettings apiGatewaySettings;

    @Autowired
    public MemberService(ApiGatewaySettings apiGatewaySettings) {
        this.apiGatewaySettings = apiGatewaySettings;
    }

    public Member get(String id) {
        try {
            HttpResponse<Member> response = Unirest.get(apiGatewaySettings.getUrl() + "/members/" + id).asObject(Member.class);
            return response.getBody();
        } catch (UnirestException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
