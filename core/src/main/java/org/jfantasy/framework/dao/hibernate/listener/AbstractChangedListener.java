package org.jfantasy.framework.dao.hibernate.listener;

import javax.annotation.PostConstruct;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractChangedListener<T>
    implements PostCommitUpdateEventListener,
        PostCommitInsertEventListener,
        PostCommitDeleteEventListener,
        ApplicationContextAware {

  private final Class<T> entityClass;
  protected ApplicationContext applicationContext;
  private final EventType[] types;

  protected AbstractChangedListener(EventType... types) {
    this.types = types;
    this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
  }

  @PostConstruct
  public void postConstruct() {
    EventListenerRegistry eventListenerRegistry =
        applicationContext.getBean(EventListenerRegistry.class);
    for (EventType type : types) {
      eventListenerRegistry.appendListeners(type, this);
    }
  }

  @Override
  public void onPostUpdateCommitFailed(PostUpdateEvent event) {}

  @Override
  public void onPostDeleteCommitFailed(PostDeleteEvent event) {}

  @Override
  public void onPostInsertCommitFailed(PostInsertEvent event) {}

  protected boolean missing(PostInsertEvent event) {
    return !event.getEntity().getClass().isAssignableFrom(entityClass);
  }

  protected boolean missing(PostUpdateEvent event) {
    return !event.getEntity().getClass().isAssignableFrom(entityClass);
  }

  protected boolean missing(PostDeleteEvent event) {
    return !event.getEntity().getClass().isAssignableFrom(entityClass);
  }

  @Override
  public void onPostInsert(PostInsertEvent event) {
    if (missing(event)) {
      return;
    }
    onPostInsert(getEntity(event), event);
  }

  @Override
  public void onPostUpdate(PostUpdateEvent event) {
    if (missing(event)) {
      return;
    }
    onPostUpdate(getEntity(event), event);
  }

  protected void onPostInsert(T entity, PostInsertEvent event) {}

  protected void onPostUpdate(T entity, PostUpdateEvent event) {}

  protected void onPostDelete(T entity, PostDeleteEvent event) {}

  @Override
  public void onPostDelete(PostDeleteEvent event) {
    if (missing(event)) {
      return;
    }
    onPostDelete(getEntity(event), event);
  }

  private T getEntity(PostInsertEvent event) {
    return entityClass.cast(event.getEntity());
  }

  private T getEntity(PostDeleteEvent event) {
    return entityClass.cast(event.getEntity());
  }

  private T getEntity(PostUpdateEvent event) {
    return entityClass.cast(event.getEntity());
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
    Class<?> aClass = ClassUtil.forName(entityPersister.getRootEntityName());
    assert aClass != null;
    return entityClass.isAssignableFrom(aClass);
  }

  protected boolean modify(PostUpdateEvent event, String property) {
    if (event.getOldState() == null) {
      return false;
    }
    int index = ObjectUtil.indexOf(event.getPersister().getPropertyNames(), property);
    if (index != -1) {
      if (event.getState()[index] != null) {
        return !event.getState()[index].equals(event.getOldState()[index]);
      } else {
        return event.getState()[index] != event.getOldState()[index];
      }
    }
    return false;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
