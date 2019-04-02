package org.jfantasy.framework.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author limaofeng
 */
@Data
@SuperBuilder
@MappedSuperclass
public abstract class BaseBusEntity implements Serializable {

    private static final long serialVersionUID = -6543503526965322995L;

    public static final String FIELDS_BY_CREATOR = "creator";
    public static final String FIELDS_BY_CREATE_TIME = "createTime";
    public static final String FIELDS_BY_MODIFIER = "modifier";
    public static final String FIELDS_BY_MODIFY_TIME = "modifyTime";
    public static final String[] BASE_FIELDS = {FIELDS_BY_CREATOR, FIELDS_BY_CREATE_TIME, FIELDS_BY_MODIFIER, FIELDS_BY_MODIFY_TIME};
    public static final String BASE_JSONFIELDS = FIELDS_BY_CREATOR + ",create_time," + FIELDS_BY_MODIFIER + ",modify_time";

    /**
     * 创建人
     */
    @Column(updatable = false, name = "CREATOR", length = 20)
    private String creator;
    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, name = "CREATE_TIME")
    private Date createTime;
    /**
     * 最后修改人
     */
    @Column(name = "MODIFIER", length = 20)
    private String modifier;
    /**
     * 最后修改时间
     */
    @JsonProperty("modify_time")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

}