package org.jfantasy.framework.search.dao;

import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.backend.EntityChangedListener;
import org.jfantasy.framework.search.backend.IndexChecker;
import org.jfantasy.framework.search.cache.IndexCache;
import org.jfantasy.framework.util.common.ClassUtil;

public class EntityChangedEventListener
    implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

  public void onPostInsert(PostInsertEvent event) {
    Object entity = event.getEntity();
    EntityPersister entityPersister = event.getPersister();
    Class<?> clazz = ClassUtil.forName(entityPersister.getRootEntityName());
    assert clazz != null;
    CuckooIndex cuckooIndex = IndexCache.getInstance().get(clazz);
    if (cuckooIndex == null) {
      return;
    }
    EntityChangedListener entityChangedListener = cuckooIndex.getEntityChangedListener();
    if (!IndexChecker.hasIndexed(clazz)) {
      return;
    }
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
    CuckooIndex cuckooIndex = IndexCache.getInstance().get(clazz);
    if (cuckooIndex == null) {
      return;
    }
    EntityChangedListener entityChangedListener = cuckooIndex.getEntityChangedListener();
    if (IndexChecker.hasIndexed(clazz)) {
      entityChangedListener.entityUpdate(entity);
    } else if (IndexChecker.needListener(clazz)) {
      entityChangedListener.getRefListener().entityChange(clazz, event.getId().toString());
    }
  }

  public void onPostDelete(PostDeleteEvent event) {
    System.out.println(event);
    //    if (!BuguIndex.isRunning()) {
    //      return;
    //    }
    //
    //    EntityPersister entityPersister = event.getPersister();
    //    Class<?> clazz = ClassUtil.forName(entityPersister.getRootEntityName());
    //    LuceneDao luceneDao = DaoCache.getInstance().get(clazz);
    //    if (luceneDao == null) {
    //      return;
    //    }
    //    EntityChangedListener luceneListener =
    // DaoCache.getInstance().get(clazz).getLuceneListener();
    //    if (luceneListener == null) {
    //      return;
    //    }
    //    if (IndexChecker.hasIndexed(clazz)) {
    //      luceneListener.entityRemove(event.getId().toString());
    //    } else if (IndexChecker.needListener(clazz)) {
    //      luceneListener.getRefListener().entityChange(clazz, event.getId().toString());
    //    }
  }
}
