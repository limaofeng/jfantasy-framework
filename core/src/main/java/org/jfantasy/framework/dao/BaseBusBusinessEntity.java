package org.jfantasy.framework.dao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.*;

/** @author limaofeng */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@MappedSuperclass
public class BaseBusBusinessEntity extends BaseBusEntity implements LogicalDeletion {

  public static final String DELETED_BY_FIELD_NAME = "deleted";

  @JsonIgnore
  @Column(name = "DELETED")
  private Boolean deleted;

  @Override
  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
