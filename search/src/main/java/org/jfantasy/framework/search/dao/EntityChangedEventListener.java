package org.jfantasy.framework.search.dao;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;

import javax.persistence.PostPersist;

public class EntityChangedEventListener {

    @PostPersist
  public void onPostInsert(PostInsertEvent event) {
//    if (!BuguIndex.isRunning()) {
//      return;
//    }
//
//    Object entity = event.getEntity();
//    EntityPersister entityPersister = event.getPersister();
//    Class<?> clazz = ClassUtil.forName(entityPersister.getRootEntityName());
//    LuceneDao luceneDao = DaoCache.getInstance().get(clazz);
//    if (luceneDao == null) {
//      return;
//    }
//    EntityChangedListener luceneListener = DaoCache.getInstance().get(clazz).getLuceneListener();
//    if (luceneListener == null) {
//      return;
//    }
//    if (!IndexChecker.hasIndexed(clazz)) {
//      return;
//    }
//    luceneListener.entityInsert(entity);
  }

//  @Override
  public boolean requiresPostCommitHanding(EntityPersister persister) {
    return false;
  }

  public void onPostUpdate(PostUpdateEvent event) {
//    if (!BuguIndex.isRunning()) {
//      return;
//    }
//
//    Object entity = event.getEntity();
//    EntityPersister entityPersister = event.getPersister();
//    Class<?> clazz = ClassUtil.forName(entityPersister.getRootEntityName());
//    LuceneDao luceneDao = DaoCache.getInstance().get(clazz);
//    if (luceneDao == null) {
//      return;
//    }
//    EntityChangedListener luceneListener = DaoCache.getInstance().get(clazz).getLuceneListener();
//    if (luceneListener == null) {
//      return;
//    }
//    if (IndexChecker.hasIndexed(clazz)) {
//      luceneListener.entityUpdate(entity);
//    } else if (IndexChecker.needListener(clazz)) {
//      luceneListener.getRefListener().entityChange(clazz, event.getId().toString());
//    }
  }

  public void onPostDelete(PostDeleteEvent event) {
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
//    EntityChangedListener luceneListener = DaoCache.getInstance().get(clazz).getLuceneListener();
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
