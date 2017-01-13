package org.jfantasy.order.rest.models;

import org.jfantasy.order.bean.enums.PayeeType;

import java.math.BigDecimal;
import java.util.List;

/**
 * 利润链
 */
public class ProfitChain {
    private String id;
    private PayeeType type;
    private String name;
    private String role;
    private String roleName;
    private BigDecimal revenue;
    private BigDecimal balance;
    private String notes;
    private String tradeNo;
    private List<ProfitChain> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PayeeType getType() {
        return type;
    }

    public void setType(PayeeType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public List<ProfitChain> getChildren() {
        return children;
    }

    public void setChildren(List<ProfitChain> children) {
        this.children = children;
    }

}
