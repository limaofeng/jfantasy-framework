package org.jfantasy.framework.dao.hibernate.interceptors;

import java.io.Serializable;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.BaseBusBusinessEntity;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 实体公共属性，自动填充拦截器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:07:30
 */
public class BusEntityInterceptor extends EmptyInterceptor {

  private static final long serialVersionUID = -3073628114943223153L;

  /** 默认编辑人 */
  private static final String DEFAULT_MODIFIER = "unknown";
  /** 默认创建人 */
  private static final String DEFAULT_CREATOR = "unknown";

  @Override
  public boolean onFlushDirty(
      Object entity,
      Serializable id,
      Object[] currentState,
      Object[] previousState,
      String[] propertyNames,
      Type[] types) {
    if (entity instanceof BaseBusEntity) {
      String modifier = DEFAULT_MODIFIER;
      LoginUser user = SpringSecurityUtils.getCurrentUser();
      if (ObjectUtil.isNotNull(user)) {
        modifier = user.getUid();
      }
      int count = 0;
      for (int i = 0; i < propertyNames.length; i++) {
        if (BaseBusEntity.FIELD_UPDATED_BY.equals(propertyNames[i])) {
          currentState[i] = modifier;
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
      String creator =
          ObjectUtil.isNotNull(user)
              ? user.getUid()
              : StringUtil.defaultValue(((BaseBusEntity) entity).getCreatedBy(), DEFAULT_CREATOR);
      int count = 0;
      int maxCount = 4;
      if (entity instanceof BaseBusBusinessEntity) {
        maxCount++;
      }
      for (int i = 0; i < propertyNames.length; i++) {
        if (BaseBusEntity.FIELD_CREATED_BY.equals(propertyNames[i])
            || BaseBusEntity.FIELD_UPDATED_BY.equals(propertyNames[i])) {
          state[i] = creator;
          count++;
        } else if (BaseBusEntity.FIELD_CREATED_AT.equals(propertyNames[i])
            || BaseBusEntity.FIELD_UPDATED_AT.equals(propertyNames[i])) {
          state[i] = DateUtil.now().clone();
          count++;
        } else if (BaseBusBusinessEntity.FIELD_DELETED.equals(propertyNames[i])) {
          state[i] = false;
          assert entity instanceof BaseBusBusinessEntity;
          ((BaseBusBusinessEntity) entity).setDeleted(false);
        }
        if (count >= maxCount) {
          return true;
        }
      }
    }
    return super.onSave(entity, id, state, propertyNames, types);
  }
}
