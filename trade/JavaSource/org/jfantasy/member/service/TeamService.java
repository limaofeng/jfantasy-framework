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
public class TeamService {

    private static final Log LOG = LogFactory.getLog(AccountService.class);

    private final ApiGatewaySettings apiGatewaySettings;

    @Autowired
    public TeamService(ApiGatewaySettings apiGatewaySettings) {
        this.apiGatewaySettings = apiGatewaySettings;
    }

    public Team get(String id) {
        try {
            HttpResponse<Team> response = Unirest.get(apiGatewaySettings.getUrl() + "/teams/" + id).asObject(Team.class);
            return response.getBody();
        } catch (UnirestException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
