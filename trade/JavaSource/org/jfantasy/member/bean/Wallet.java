package org.jfantasy.member.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 钱包
 */
@Entity
@Table(name = "MEM_WALLET")
@TableGenerator(name = "wallet_gen", table = "sys_sequence", pkColumnName = "gen_name", pkColumnValue = "mem_wallet:id", valueColumnName = "gen_value")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Wallet extends BaseBusEntity {

    private static final long serialVersionUID = -841277262755651990L;

    /**
     * 钱包ID
     */
    @Id
    @Column(name = "ID", updatable = false)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "wallet_gen")
    private Long id;
    /**
     * 会员
     */
    @Column(name = "MEMBER_ID", unique = true, updatable = false)
    private Long memberId;
    /**
     * 账户余额
     */
    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;
    /**
     * 累积收入
     */
    @Column(name = "INCOME", nullable = false)
    private BigDecimal income;
    /**
     * 资金账户
     */
    @Column(name = "ACCOUNT", nullable = false, updatable = false, unique = true)
    private String account;
    /**
     * 有效的积分
     */
    @Column(name = "POINTS", nullable = false)
    private Long points;
    /**
     * 成长值
     */
    @Column(name = "GROWTH", nullable = false)
    private Long growth;
    /**
     * 绑卡数量
     */
    @Column(name = "CARDS", nullable = false)
    private Long cards;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getGrowth() {
        return growth;
    }

    public void setGrowth(Long growth) {
        this.growth = growth;
    }

    public Long getCards() {
        return cards;
    }

    public void setCards(Long cards) {
        this.cards = cards;
    }
}
