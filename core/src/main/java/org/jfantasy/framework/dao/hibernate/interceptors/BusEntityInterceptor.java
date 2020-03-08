package org.jfantasy.framework.dao.hibernate.interceptors;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.BaseBusBusinessEntity;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;

import java.io.Serializable;

/**
 * 实体公共属性，自动填充拦截器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:07:30
 */
public class BusEntityInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = -3073628114943223153L;

    /**
     * 默认编辑人
     */
    private static final String DEFAULT_MODIFIER = "unknown";
    /**
     * 默认创建人
     */
    private static final String DEFAULT_CREATOR = "unknown";

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (entity instanceof BaseBusEntity) {
            String modifier = DEFAULT_MODIFIER;
            LoginUser user = SpringSecurityUtils.getCurrentUser();
            if (ObjectUtil.isNotNull(user)) {
                modifier = user.getUid();
            }
            int count = 0;
            for (int i = 0; i < propertyNames.length; i++) {
                if ("modifier".equals(propertyNames[i])) {
                    currentState[i] = modifier;
                    count++;
                } else if ("updatedAt".equals(propertyNames[i])) {
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
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        if (entity instanceof BaseBusEntity) {
            LoginUser user = SpringSecurityUtils.getCurrentUser();
            String creator = ObjectUtil.isNotNull(user) ? user.getUid() : StringUtil.defaultValue(((BaseBusEntity) entity).getCreator(), DEFAULT_CREATOR);
            int count = 0;
            int maxCount = 4;
            if (entity instanceof BaseBusBusinessEntity) {
                maxCount++;
            }
            for (int i = 0; i < propertyNames.length; i++) {
                if ("creator".equals(propertyNames[i]) || "modifier".equals(propertyNames[i])) {
                    state[i] = creator;
                    count++;
                } else if ("createdAt".equals(propertyNames[i]) || "updatedAt".equals(propertyNames[i])) {
                    state[i] = DateUtil.now().clone();
                    count++;
                } else if ("deleted".equals(propertyNames[i])) {
                    state[i] = false;
                    // TODO: 2019/4/24  遗留问题待解决
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