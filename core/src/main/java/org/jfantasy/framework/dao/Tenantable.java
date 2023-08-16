package org.jfantasy.framework.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.ClassUtil;

/**
 * 支持多租户属性的实体接口。
 *
 * @author limaofeng
 */
public interface Tenantable {

  Map<Class<?>, String> filedNameCache = new ConcurrentHashMap<>();
  String TENANT_BY_FIELD_NAME = "tenantId";

  static String getTenantFieldName(Class<?> domainClass) {
    return filedNameCache.computeIfAbsent(
        domainClass,
        key -> {
          try {
            return ClassUtil.getFieldValue(domainClass, "TENANT_BY_FIELD_NAME");
          } catch (IgnoreException e) {
            return TENANT_BY_FIELD_NAME;
          }
        });
  }

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
