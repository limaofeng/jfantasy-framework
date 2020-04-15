package org.jfantasy.framework.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author limaofeng
 */
@Data
@MappedSuperclass
public class BaseBusBusinessEntity extends BaseBusEntity {

    public static final String FIELD_DELETED = "deleted";

    @JsonIgnore
    @Column(name = "DELETED")
    private Boolean deleted;
}