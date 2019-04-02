package org.jfantasy.framework.dao;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * @author limaofeng
 */
@Data
@SuperBuilder
@MappedSuperclass
public abstract class BaseBusBusinessEntity extends BaseBusEntity {

    @Column(name = "DELETED")
    private Boolean deleted;
}