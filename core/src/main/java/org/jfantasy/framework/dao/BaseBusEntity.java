package org.jfantasy.framework.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

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

    public static final String FIELD_CREATOR = "creator";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_UPDATOR = "updator";
    public static final String FIELD_UPDATED_AT = "updatedAt";
    public static final String[] ALL_FIELD = {FIELD_CREATOR, FIELD_CREATED_AT, FIELD_UPDATOR, FIELD_UPDATED_AT};
    public static final String ALL_FIELD_STR = Arrays.stream(ALL_FIELD).collect(Collectors.joining(","));

    /**
     * 创建人
     */
    @Column(updatable = false, name = "CREATOR", length = 20)
    private String creator;
    /**
     * 创建时间
     */
    @JsonProperty("createdAt")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, name = "CREATED_AT")
    private Date createdAt;
    /**
     * 最后修改人
     */
    @Column(name = "UPDATOR", length = 20)
    private String updator;
    /**
     * 最后修改时间
     */
    @JsonProperty("updatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @Deprecated
    @Column(name = "UPDATOR", length = 20)
    private String modifier;

    @Deprecated
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, name = "CREATE_TIME")
    private Date createTime;

    @Deprecated
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

}