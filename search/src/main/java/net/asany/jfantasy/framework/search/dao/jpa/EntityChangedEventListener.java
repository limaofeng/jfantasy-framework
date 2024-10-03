/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.search.dao.jpa;

import net.asany.jfantasy.framework.search.backend.EntityChangedListener;
import net.asany.jfantasy.framework.search.backend.IndexChecker;
import net.asany.jfantasy.framework.search.cache.DaoCache;
import net.asany.jfantasy.framework.search.dao.CuckooDao;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;

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
  public boolean requiresPostCommitHandling(EntityPersister persister) {
    // TODO: 评估 requiresPostCommitHandling 对逻辑的影响
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
