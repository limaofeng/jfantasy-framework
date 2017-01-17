package org.jfantasy.order.rest.models;

import org.jfantasy.order.bean.enums.PayeeType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 利润链
 */
public class ProfitChain {
    private String id;//流程ID
    private PayeeType type;//收款人类型
    private String payee;//收款人
    private String name;//收款人名称
    private String role;//收款人编码
    private String roleName;//收款人Title
    private String project;//收款项目
    private String projectName;//收款项目名称
    private BigDecimal revenue;//收益
    private BigDecimal balance;//最终余额
    private String notes;//备注
    private String tradeNo;//关联交易
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
        return balance == null ? revenue : balance;
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

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public void addChild(ProfitChain profitChain) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(profitChain);
        this.setBalance(this.getBalance().subtract(profitChain.getRevenue()));
    }
}
