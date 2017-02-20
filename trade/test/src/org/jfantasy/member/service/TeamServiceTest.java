package org.jfantasy.member.service;

import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.junit.Assert;
import org.junit.Test;


public class TeamServiceTest {

    private TeamService teamService ;
    {
        ApiGatewaySettings apiGatewaySettings = new ApiGatewaySettings();
        apiGatewaySettings.setUrl("http://v2.zbsg.com.cn:8000");
        teamService = new TeamService(apiGatewaySettings);
    };

    @Test
    public void get() throws Exception {
        Team team = teamService.get("who");
        Assert.assertEquals(team.getKey(),"who");
    }

}