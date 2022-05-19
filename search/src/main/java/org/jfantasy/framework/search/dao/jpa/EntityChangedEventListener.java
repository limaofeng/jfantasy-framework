package org.jfantasy.framework.search.dao.jpa;

import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.jfantasy.framework.search.backend.EntityChangedListener;
import org.jfantasy.framework.search.backend.IndexChecker;
import org.jfantasy.framework.search.cache.DaoCache;
import org.jfantasy.framework.search.dao.CuckooDao;
import org.jfantasy.framework.util.common.ClassUtil;

public class EntityChangedEventListener
    implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

  public void onPostInsert(PostInsertEvent event) {
    Object entity = event.getEntity();
    EntityPersister entityPersister = event.getPersister();
    Class<?> clazz = ClassUtil.forName(entityPersister.getRootEntityName());
    assert clazz != null;
    if (!IndexChecker.hasIndexed(clazz)) {
      return;
    }
    CuckooDao cuckooDao = DaoCache.getInstance().get(clazz);
    EntityChangedListener entityChangedListener = cuckooDao.getEntityChangedListener();
    entityChangedListener.entityInsert(entity);
  }

  @Override
  public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
    return false;
  }

  public void onPostUpdate(PostUpdateEvent event) {
    Object entity = event.getEntity();
    EntityPersister entityPersister = event.getPersister();
    Class<?> clazz = ClassUtil.forName(entityPersister.getRootEntityName());
    assert clazz != null;
    DaoCache daoCache = DaoCache.getInstance();
    if (IndexChecker.hasIndexed(clazz)) {
      CuckooDao cuckooDao = daoCache.get(clazz);
      EntityChangedListener entityChangedListener = cuckooDao.getEntityChangedListener();
      entityChangedListener.entityUpdate(entity);
    } else if (IndexChecker.needListener(clazz)) {
      CuckooDao cuckooDao = daoCache.get(clazz, daoCache::buildDao);
      EntityChangedListener entityChangedListener = cuckooDao.getEntityChangedListener();
      entityChangedListener.getRefListener().entityChange(clazz, event.getId().toString());
    }
  }

  public void onPostDelete(PostDeleteEvent event) {
    EntityPersister entityPersister = event.getPersister();
    Class<?> clazz = ClassUtil.forName(entityPersister.getRootEntityName());
    assert clazz != null;
    DaoCache daoCache = DaoCache.getInstance();
    if (IndexChecker.hasIndexed(clazz)) {
      CuckooDao cuckooDao = daoCache.get(clazz);
      EntityChangedListener entityChangedListener = cuckooDao.getEntityChangedListener();
      entityChangedListener.entityRemove(event.getId().toString());
    } else if (IndexChecker.needListener(clazz)) {
      CuckooDao cuckooDao = daoCache.get(clazz, daoCache::buildDao);
      EntityChangedListener entityChangedListener = cuckooDao.getEntityChangedListener();
      entityChangedListener.getRefListener().entityChange(clazz, event.getId().toString());
    }
  }
}
