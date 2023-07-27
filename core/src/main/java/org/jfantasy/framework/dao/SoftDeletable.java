package org.jfantasy.framework.dao;

import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.ClassUtil;

/**
 * 逻辑删除接口
 *
 * @author limaofeng
 */
public interface SoftDeletable {

  String DELETED_BY_FIELD_NAME = "deleted";

  /**
   * 是否已经删除
   *
   * @param b 是否已经删除
   */
  void setDeleted(boolean b);

  /**
   * 是否已经删除
   *
   * @return 是否已经删除
   */
  boolean isDeleted();

  /**
   * 是否已经删除
   *
   * @param domainClass 实体类
   * @return 是否已经删除
   */
  static String getDeletedFieldName(Class<?> domainClass) {
    try {
      return ClassUtil.getFieldValue(domainClass, "DELETED_BY_FIELD_NAME");
    } catch (IgnoreException e) {
      return DELETED_BY_FIELD_NAME;
    }
  }
}
