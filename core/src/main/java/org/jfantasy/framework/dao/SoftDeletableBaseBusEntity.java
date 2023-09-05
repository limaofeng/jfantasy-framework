package org.jfantasy.framework.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 可软删除的实体基类
 *
 * @author limaofeng
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
public class SoftDeletableBaseBusEntity extends BaseBusEntity implements SoftDeletable {

  public static final String DELETED_BY_FIELD_NAME = "deleted";

  @JsonIgnore
  @Column(name = "DELETED")
  private Boolean deleted;

  @Override
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  @Override
  public boolean isDeleted() {
    return Boolean.TRUE.equals(deleted);
  }
}
