package org.jfantasy.framework.dao.hibernate.interceptors;

import java.io.Serializable;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.dao.SoftDeletable;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.util.common.DateUtil;

/**
 * 实体公共属性，自动填充拦截器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:07:30
 */
public class BusEntityInterceptor extends EmptyInterceptor {

  @Override
  public boolean onFlushDirty(
      Object entity,
      Serializable id,
      Object[] currentState,
      Object[] previousState,
      String[] propertyNames,
      Type[] types) {
    if (entity instanceof BaseBusEntity) {
      LoginUser user = SpringSecurityUtils.getCurrentUser();
      Long modifiedBy = ((BaseBusEntity) entity).getUpdatedBy();
      if (modifiedBy == null && user != null) {
        modifiedBy = user.getUid();
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
    return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
  }

  @Override
  public boolean onSave(
      Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
    if (entity instanceof BaseBusEntity) {
      LoginUser user = SpringSecurityUtils.getCurrentUser();
      Long createdBy = ((BaseBusEntity) entity).getCreatedBy();
      if (createdBy == null && user != null) {
        createdBy = user.getUid();
      }
      int count = 0;
      int maxCount = 4;
      String deletedFieldName = "";

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
        }
        if (count >= maxCount) {
          return true;
        }
      }
    }
    return super.onSave(entity, id, state, propertyNames, types);
  }
}
