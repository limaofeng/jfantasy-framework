package org.jfantasy.trade.event;


import org.jfantasy.trade.bean.Account;
import org.springframework.context.ApplicationEvent;

public class AccountNewEvent extends ApplicationEvent {

    public AccountNewEvent(Account account) {
        super(account);
    }

    public Account getAccount() {
        return (Account) this.getSource();
    }

}
