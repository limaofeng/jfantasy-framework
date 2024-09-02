package net.asany.jfantasy.framework.dao.hibernate.event;

import net.asany.jfantasy.framework.dao.BaseBusEntity;
import net.asany.jfantasy.framework.dao.SoftDeletable;
import net.asany.jfantasy.framework.dao.Tenantable;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.SpringSecurityUtils;
import net.asany.jfantasy.framework.util.common.DateUtil;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

public class BaseBusEntityEventListener implements PreInsertEventListener, PreUpdateEventListener {

  @Override
  public boolean onPreInsert(PreInsertEvent event) {
    Object entity = event.getEntity();
    LoginUser user = SpringSecurityUtils.getCurrentUser();
    if (entity instanceof Tenantable tenantable) {
      String tenantId = ((Tenantable) entity).getTenantId();
      if (tenantId == null && user != null) {
        tenantId = user.getTenantId();
      }
      tenantable.setTenantId(tenantId);
    }
    if (entity instanceof SoftDeletable softDeletable) {
      softDeletable.setDeleted(false);
    }
    if (entity instanceof BaseBusEntity busEntity) {
      if (busEntity.getCreatedAt() == null) {
        busEntity.setCreatedAt(DateUtil.now());
      }
      if (busEntity.getUpdatedAt() == null) {
        busEntity.setUpdatedAt(DateUtil.now());
      }
      if (busEntity.getCreatedBy() == null && user != null) {
        busEntity.setCreatedBy(user.getId());
      }
      if (busEntity.getUpdatedBy() == null && user != null) {
        busEntity.setUpdatedBy(user.getId());
      }
    }
    return false;
  }

  @Override
  public boolean onPreUpdate(PreUpdateEvent event) {
    Object entity = event.getEntity();
    LoginUser user = SpringSecurityUtils.getCurrentUser();
    if (entity instanceof BaseBusEntity busEntity) {
      busEntity.setUpdatedAt(DateUtil.now());
      busEntity.setUpdatedBy(user.getId());
    }
    return false;
  }
}
