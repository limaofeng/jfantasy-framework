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
package net.asany.jfantasy.framework.search.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.asany.jfantasy.framework.search.annotations.Indexed;
import net.asany.jfantasy.framework.search.dao.CuckooDao;
import net.asany.jfantasy.framework.search.dao.jpa.JpaDefaultCuckooDao;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.SchedulingTaskExecutor;

public class DaoCache {

  private final ApplicationContext applicationContext;
  private final SchedulingTaskExecutor executor;
  private static DaoCache instance;
  private final Map<Class<?>, CuckooDao> cache;

  private DaoCache(ApplicationContext applicationContext, SchedulingTaskExecutor executor) {
    this.cache = new ConcurrentHashMap<>();
    this.applicationContext = applicationContext;
    this.executor = executor;
  }

  public static DaoCache getInstance() {
    return instance;
  }

  public static DaoCache getInstance(
      ApplicationContext applicationContext, SchedulingTaskExecutor executor) {
    return DaoCache.instance = new DaoCache(applicationContext, executor);
  }

  public boolean containsKey(Class<?> clazz) {
    return this.cache.containsKey(clazz);
  }

  public <T> CuckooDao get(Class<T> clazz) {
    return this.cache.get(clazz);
  }

  public <T> CuckooDao get(Class<T> clazz, Function<Class<?>, CuckooDao> creator) {
    return this.cache.computeIfAbsent(clazz, creator);
  }

  public <T> void put(Class<T> clazz, CuckooDao dao) {
    this.cache.put(clazz, dao);
  }

  public CuckooDao buildDao(Class<?> clazz) {
    Indexed indexed = clazz.getAnnotation(Indexed.class);
    Class<? extends CuckooDao> daoClass =
        indexed == null ? JpaDefaultCuckooDao.class : indexed.dao();
    if (ClassUtil.isAssignable(JpaDefaultCuckooDao.class, daoClass)) {
      return ClassUtil.newInstance(
          daoClass,
          new Class[] {ApplicationContext.class, Class.class, TaskExecutor.class},
          new Object[] {applicationContext, clazz, this.executor});
    }
    boolean existent = applicationContext.getBeanNamesForType(daoClass).length > 0;
    if (existent) {
      return applicationContext.getBean(daoClass);
    }
    return ClassUtil.newInstance(daoClass);
  }
}
