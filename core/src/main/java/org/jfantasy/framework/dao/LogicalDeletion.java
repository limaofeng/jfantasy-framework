package org.jfantasy.framework.dao;

import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.ClassUtil;

public interface LogicalDeletion {

  String DELETED_BY_FIELD_NAME = "deleted";

  void setDeleted(boolean b);

  static String getDeletedFieldName(Class domainClass) {
    try {
      return ClassUtil.getFieldValue(domainClass, "DELETED_BY_FIELD_NAME");
    } catch (IgnoreException e) {
      return DELETED_BY_FIELD_NAME;
    }
  }
}
