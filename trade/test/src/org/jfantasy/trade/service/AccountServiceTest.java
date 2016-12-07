package org.jfantasy.trade.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.hibernate.Session;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.lucene.dao.hibernate.OpenSessionUtils;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.pay.PayServerApplication;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.enums.AccountType;
import org.jfantasy.trade.dao.AccountDao;
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
    @Autowired
    private AccountDao accountDao;

    @Test
    public void ownerByMember() {
        Pager<Account> pager = accountService.findPager(new Pager<>(1000), new ArrayList<>());

        for (Account account : pager.getPageItems()) {
            if (AccountType.enterprise == account.getType() || AccountType.personal == account.getType()) {
                updateOwnerByMember(account);
            }
        }
    }

    private void updateOwnerByMember(Account account) {
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

                String ownerId = "";
                String ownerType = "";
                String ownerName = "";

                if ("personal".equals(memberType)) {//个人
                    ownerId = memberId.toString();
                    ownerType = "personal";
                    ownerName = name;
                } else if ("doctor".equals(memberType)) {//医生
                    ownerId = targetId;
                    ownerType = targetType;
                    ownerName = name;
                } else if (ObjectUtil.exists(new String[]{"company", "pharmacy", "clinic"}, memberType)) {//集团
                    ownerId = targetId;
                    ownerType = memberType;
                    ownerName = name;
                }

                if (StringUtil.isBlank(ownerId) || StringUtil.isBlank(ownerType) || StringUtil.isBlank(ownerName)) {
                    return;
                }

                Session session = OpenSessionUtils.openSession();
                try {
                    accountDao.batchSQLExecute("update pay_account set owner = ?,owner_id = ?,owner_type = ?,owner_name = ? where id = ?", memberId.toString(), ownerId, ownerType, ownerName, account.getSn());
                } finally {
                    OpenSessionUtils.closeSession(session);
                }

            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }

}