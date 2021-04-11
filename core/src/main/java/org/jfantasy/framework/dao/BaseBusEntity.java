package org.jfantasy.framework.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author limaofeng
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class BaseBusEntity implements Serializable {

    private static final long serialVersionUID = -6543503526965322995L;

    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_UPDATED_BY = "updatedBy";
    public static final String FIELD_UPDATED_AT = "updatedAt";
    public static final String[] ALL_FIELD = {FIELD_CREATED_BY, FIELD_CREATED_AT, FIELD_UPDATED_BY, FIELD_UPDATED_AT};
    public static final String ALL_FIELD_STR = String.join(",", ALL_FIELD);

    /**
     * 创建人
     */
    @Column(updatable = false, name = "CREATOR", length = 20)
    private String createdBy;
    /**
     * 创建时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, name = "CREATED_AT")
    private Date createdAt;
    /**
     * 最后修改人
     */
    @Column(name = "UPDATOR", length = 20)
    private String updatedBy;
    /**
     * 最后修改时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT")
    private Date updatedAt;


    @Deprecated
    @Transient
    public String getCreator() {
        return getCreatedBy();
    }

    @Deprecated
    @Transient
    public void setCreator(String creator) {
        this.setCreatedBy(creator);
    }

    @Deprecated
    @Transient
    public String getUpdator() {
        return getUpdatedBy();
    }

    @Deprecated
    @Transient
    public void setUpdator(String updator) {
        this.setUpdatedBy(updator);
    }

    @Deprecated
    @Transient
    public String getModifier() {
        return getUpdatedBy();
    }

    @Deprecated
    @Transient
    public void setModifier(String modifier) {
        this.setUpdatedBy(modifier);
    }

    @Deprecated
    @Transient
    public Date getCreateTime() {
        return this.getCreatedAt();
    }

    @Deprecated
    @Transient
    public void setCreateTime(Date createTime) {
        this.setCreatedAt(createTime);
    }

    @Deprecated
    @Transient
    public Date getModifyTime() {
        return this.getUpdatedAt();
    }

    @Deprecated
    @Transient
    public void setModifyTime(Date modifyTime) {
        this.setUpdatedAt(modifyTime);
    }

}