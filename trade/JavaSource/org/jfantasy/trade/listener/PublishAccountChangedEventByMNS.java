package org.jfantasy.trade.listener;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.ms.EventEmitter;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.event.AccountAmountChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 发送消息 - 阿里云
 */
@Component
public class PublishAccountChangedEventByMNS implements ApplicationListener<AccountAmountChangedEvent> {

    private EventEmitter eventEmitter;

    @Override
    public void onApplicationEvent(AccountAmountChangedEvent event) {
        Account account = event.getAccount();
        eventEmitter.fireEvent("account.change", account.getSn(), String.format("账户[%s]金额变动", account.getSn()), JSON.serialize(account));
    }

    @Autowired
    public void setEventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

}
