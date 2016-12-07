package org.jfantasy.trade.bean;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.trade.bean.enums.ProjectType;

import javax.persistence.*;

/**
 * 交易项目
 */
@Entity
@Table(name = "PAY_PROJECT")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
public class Project extends BaseBusEntity {

    public static final String PAYMENT = "payment";// 订单支付
    public static final String REFUND = "refund";// 订单退款
    public static final String INCOME = "income";// 收益
    public static final String INPOUR = "inpour";// 卡充值
    public static final String WITHDRAWAL = "withdrawal";// 提现
    public static final String TRANSFER = "transfer";// 转账
    public static final String RECHARGE = "recharge";// 充值


    private static final long serialVersionUID = -2461534613032864306L;

    /**
     * 编码
     */
    @Id
    @Column(name = "CODE", updatable = false,length = 15)
    private String key;
    /**
     * 项目类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE",length = 10)
    private ProjectType type;
    /**
     * 名称
     */
    @Column(name = "NAME",length = 50)
    private String name;
    /**
     * 描述
     */
    @Column(name = "DESCRIPTION")
    private String description;

    public Project(String key) {
        this.key = key;
    }

    public Project() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

}
