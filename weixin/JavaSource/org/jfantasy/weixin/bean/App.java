package org.jfantasy.weixin.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.weixin.framework.session.WeixinApp;

import javax.persistence.*;

/**
 * 微信公众号设置
 * Created by zzzhong on 2014/6/18.
 */
@Entity
@Table(name = "WEIXIN_APP")
@JsonIgnoreProperties({"hibernate_lazy_initializer", "handler"})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class App extends BaseBusEntity implements WeixinApp {

    private static final long serialVersionUID = 3392012340669466368L;

    /**
     * 开发者凭证
     **/
    @Id
    @Column(name = "ID", length = 20)
    private String id;
    /**
     * 公众号类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 10)
    private Type type;
    /**
     * 原始ID
     */
    @Column(name = "PRIMITIVE_ID", length = 32)
    private String primitiveId;
    /**
     * 密钥
     **/
    @Column(name = "APP_SECRET", length = 32)
    private String secret;
    /**
     * 公众号名称
     */
    @Column(name = "NAME", length = 50)
    public String name;
    /**
     * 微信服务器配置的token
     */
    @Column(name = "TOKEN_NAME", length = 100)
    private String token;
    /**
     * 微信生成的 ASEKey
     */
    @Column(name = "AES_KEY", length = 100)
    private String aesKey;
    /**
     * 代理ID<br/>
     * 企业号才需要配置该属性
     */
    @Column(name = "AGENT_ID")
    private Integer agentId;

    public void setAppId(String id) {
        this.id = id;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String getAesKey() {
        return this.aesKey;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPrimitiveId() {
        return primitiveId;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Integer getAgentId() {
        return this.agentId;
    }

    public void setAgentId(Integer agentId) {
        this.agentId = agentId;
    }

    public void setPrimitiveId(String primitiveId) {
        this.primitiveId = primitiveId;
    }
}
