package org.jfantasy.framework.dao.hibernate.listener;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class AbstractChangedListener<T>
    implements PostCommitUpdateEventListener,
        PostCommitInsertEventListener,
        PostCommitDeleteEventListener {

  private static final long serialVersionUID = -3358937507937580406L;

  private Class<T> entityClass;

  protected transient ApplicationContext applicationContext;
  private transient EventListenerRegistry eventListenerRegistry;
  private transient EventType[] types = new EventType[0];

  protected AbstractChangedListener(EventType... types) {
    this.types = types;
    this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
  }

  @PostConstruct
  @SuppressWarnings("unchecked")
  public void postConstruct() {
    for (EventType type : types) {
      this.eventListenerRegistry.appendListeners(type, this);
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
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    Class<?> aClass = ClassUtil.forName(persister.getRootEntityName());
    assert aClass != null;
    return entityClass.isAssignableFrom(aClass);
  }

  protected boolean modify(PostUpdateEvent event, String property) {
    if (event.getOldState() == null) {
      return false;
    }
    Arrays.binarySearch(event.getPersister().getPropertyNames(), property);
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

  @Autowired
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Autowired
  public void setEventListenerRegistry(EventListenerRegistry eventListenerRegistry) {
    this.eventListenerRegistry = eventListenerRegistry;
  }
}
