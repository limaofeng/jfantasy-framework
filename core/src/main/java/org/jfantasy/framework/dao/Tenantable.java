package org.jfantasy.framework.dao;

/**
 * 支持多租户属性的实体接口。
 *
 * @author limaofeng
 */
public interface Tenantable {
  /**
   * 获取实体所属的租户ID。
   *
   * @return 租户ID
   */
  String getTenantId();

  /**
   * 设置实体所属的租户ID。
   *
   * @param tenantId 租户ID
   */
  void setTenantId(String tenantId);
}
