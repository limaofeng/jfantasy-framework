package org.jfantasy.pay.ons;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.fasterxml.jackson.databind.JsonNode;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.member.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class PayMessageListener implements MessageListener {

    @Autowired
    private WalletService walletService;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        switch (message.getTag()) {
            case "account":
                walletService.saveOrUpdateWallet(JSON.deserialize(new String(message.getBody())));
                break;
            case "card_bind":
                this.cardBind(message);
                break;
            default:
        }
        return Action.CommitMessage;
    }

    private void cardBind(Message message) {
        JsonNode cardbind = JSON.deserialize(new String(message.getBody()));
        assert cardbind != null;
        String account = cardbind.get("account").asText();
        JsonNode extras = cardbind.get("extras");
        Map<String, Object> data = new HashMap<>();
        if (extras != null) {
            for (int i = 0; i < extras.size(); i++) {
                JsonNode extra = extras.get(i);
                data.put(extra.get("project").asText(), extra.get("value").asInt());
            }
        }
        walletService.addCard(account, data);
    }

}