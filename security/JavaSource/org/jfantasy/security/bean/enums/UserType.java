package org.jfantasy.security.bean.enums;

public enum UserType {
    employee("员工"), admin("管理员");

    private String value;

    private UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
