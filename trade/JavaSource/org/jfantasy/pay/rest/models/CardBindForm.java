package org.jfantasy.pay.rest.models;

public class CardBindForm {

    /**
     * 密码
     */
    private String password;
    /**
     * 充值用户
     */
    private Long memberId;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
