package org.jfantasy.framework.search.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.jfantasy.framework.search.annotations.Indexed;
import org.jfantasy.framework.search.dao.CuckooDao;
import org.jfantasy.framework.search.dao.jpa.JpaDefaultCuckooDao;
import org.jfantasy.framework.util.common.ClassUtil;
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
