package org.jfantasy.member.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.UnirestObjectMapper;
import org.junit.Test;

public class MemberServiceTest {

    @Test
    public void get() throws Exception {
        Unirest.setObjectMapper(new UnirestObjectMapper(JSON.getObjectMapper()));

        HttpResponse<Member> response = Unirest.get("http://114.55.142.155:8000/members/14").asObject(Member.class);
        Member member = response.getBody();
        System.out.println(member.getType());
        System.out.println(member.getId().toString());
        System.out.println(member.getNickName());
    }

}