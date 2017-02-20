package org.jfantasy.pay.rest.models;

import org.jfantasy.trade.bean.enums.AccountType;

public class AccountForm {
    private String owner;
    private AccountType type;
    private String password;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPassword() {
        return password;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
