package org.jfantasy.trade.event;


import org.jfantasy.trade.bean.Account;
import org.springframework.context.ApplicationEvent;

public class AccountChangedEvent extends ApplicationEvent {

    public AccountChangedEvent(Account account) {
        super(account);
    }

    public Account getAccount() {
        return (Account) this.getSource();
    }

}
