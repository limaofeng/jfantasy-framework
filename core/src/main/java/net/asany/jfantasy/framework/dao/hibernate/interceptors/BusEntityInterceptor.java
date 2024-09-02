package net.asany.jfantasy.framework.dao.hibernate.interceptors;

import net.asany.jfantasy.framework.dao.BaseBusEntity;
import net.asany.jfantasy.framework.dao.SoftDeletable;
import net.asany.jfantasy.framework.dao.Tenantable;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.SpringSecurityUtils;
import net.asany.jfantasy.framework.util.common.DateUtil;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

/**
 * 实体公共属性，自动填充拦截器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:07:30
 */
public class BusEntityInterceptor implements Interceptor {

  @Override
  public boolean onFlushDirty(
      Object entity,
      Object id,
      Object[] currentState,
      Object[] previousState,
      String[] propertyNames,
      Type[] types) {
    if (entity instanceof BaseBusEntity) {
      LoginUser user = SpringSecurityUtils.getCurrentUser();
      Long modifiedBy = ((BaseBusEntity) entity).getUpdatedBy();
      if (modifiedBy == null && user != null) {
        modifiedBy = user.getId();
      }
      int count = 0;
      for (int i = 0; i < propertyNames.length; i++) {
        if (BaseBusEntity.FIELD_UPDATED_BY.equals(propertyNames[i])) {
          currentState[i] = modifiedBy;
          count++;
        } else if (BaseBusEntity.FIELD_UPDATED_AT.equals(propertyNames[i])) {
          currentState[i] = DateUtil.now().clone();
          count++;
        }
        if (count >= 2) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean onSave(
      Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
    if (entity instanceof BaseBusEntity) {
      LoginUser user = SpringSecurityUtils.getCurrentUser();
      Long createdBy = ((BaseBusEntity) entity).getCreatedBy();
      if (createdBy == null && user != null) {
        createdBy = user.getId();
      }
      int count = 0;
      int maxCount = 4;
      String deletedFieldName = "";
      String tenantFieldName = "";
      String tenantId = "";

      if (entity instanceof Tenantable) {
        tenantFieldName = Tenantable.getTenantFieldName(entity.getClass());
        tenantId = ((Tenantable) entity).getTenantId();
        if (tenantId == null && user != null) {
          tenantId = user.getTenantId();
        }
        maxCount++;
      }
      if (entity instanceof SoftDeletable) {
        deletedFieldName = SoftDeletable.getDeletedFieldName(entity.getClass());
        maxCount++;
      }
      for (int i = 0; i < propertyNames.length; i++) {
        if (BaseBusEntity.FIELD_CREATED_BY.equals(propertyNames[i])
            || BaseBusEntity.FIELD_UPDATED_BY.equals(propertyNames[i])) {
          state[i] = createdBy;
          count++;
        } else if (BaseBusEntity.FIELD_CREATED_AT.equals(propertyNames[i])
            || BaseBusEntity.FIELD_UPDATED_AT.equals(propertyNames[i])) {
          state[i] = DateUtil.now().clone();
          count++;
        } else if (deletedFieldName.equals(propertyNames[i])) {
          state[i] = false;
          assert entity instanceof SoftDeletable;
          ((SoftDeletable) entity).setDeleted(false);
        } else if (tenantFieldName.equals(propertyNames[i])) {
          state[i] = tenantId;
          assert entity instanceof Tenantable;
          ((Tenantable) entity).setTenantId(tenantId);
        }
        if (count >= maxCount) {
          return true;
        }
      }
    }
    return false;
  }
}
