package org.jfantasy.trade.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.pay.PayServerApplication;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.enums.AccountType;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
@ActiveProfiles("dev")
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Test
    public void ownerByMember() {
        Pager<Account> pager = accountService.findPager(new Pager<>(1000), new ArrayList<>());

        for (Account account : pager.getPageItems()) {
            if (AccountType.enterprise == account.getType() || AccountType.personal == account.getType()) {
                ownerByMember(account);
                this.accountService.update(account);
            }
        }

    }

    private void ownerByMember(Account account) {
        try {
            String[] asr = account.getOwner().split(":");
            String username = asr[1];
            HttpResponse<JsonNode> postResponse = Unirest.get("http://114.55.142.155:8000/members?EQS_username=" + username).asJson();
            JsonNode jsonNode = postResponse.getBody();
            JSONObject pager = jsonNode.getObject();

            if (pager.getInt("count") == 1) {
                JSONObject member = jsonNode.getObject().getJSONArray("items").getJSONObject(0);
                Long memberId = member.getLong("id");
                String memberType = member.getString("type");
                String targetType = member.has("target_type") ? member.getString("target_type") : null;
                String targetId = member.has("target_id") ? member.getString("target_id") : null;
                String name = member.getString("nick_name");
                if ("personal".equals(memberType)) {//个人
                    account.setOwnerId(memberId.toString());
                    account.setOwnerType("personal");
                    account.setOwnerName(name);
                } else if ("doctor".equals(memberType)) {//医生
                    account.setOwnerId(targetId);
                    account.setOwnerType(targetType);
                    account.setOwnerName(name);
                } else if (ObjectUtil.exists(new String[]{"company", "pharmacy", "clinic"}, memberType)) {//集团
                    account.setOwnerId(targetId);
                    account.setOwnerType(memberType);
                    account.setOwnerName(name);
                }
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

}