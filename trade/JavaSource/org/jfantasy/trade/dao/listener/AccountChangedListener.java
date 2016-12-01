package org.jfantasy.trade.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.event.AccountChangedEvent;
import org.springframework.stereotype.Component;

@Component
public class AccountChangedListener extends AbstractChangedListener<Account> {

    private static final long serialVersionUID = 6923717125278747287L;

    public AccountChangedListener() {
        super(EventType.POST_COMMIT_INSERT,EventType.POST_COMMIT_UPDATE);
    }

    @Override
    public void onPostInsert(Account account, PostInsertEvent event) {
        applicationContext.publishEvent(new AccountChangedEvent(account));
    }

    @Override
    public void onPostUpdate(Account account, PostUpdateEvent event) {
        if (modify(event, "amount")) {
            applicationContext.publishEvent(new AccountChangedEvent(account));
        }
    }

}
