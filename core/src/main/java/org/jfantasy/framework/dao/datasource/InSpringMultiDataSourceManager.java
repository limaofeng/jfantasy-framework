package org.jfantasy.framework.dao.datasource;

import java.util.*;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 在 Spring ，通过 Spring BeanName 中查找 DataSource
 *
 * @author limaofeng
 */
@Slf4j
public class InSpringMultiDataSourceManager extends AbstractMultiDataSourceManager
    implements MultiDataSourceManager, ApplicationContextAware {

  private ApplicationContext applicationContext;

  private final Map<Object, Object> dataSourceMap =
      new Map<Object, Object>() {
        @Override
        public int size() {
          return applicationContext.getBeanNamesForType(DataSource.class).length;
        }

        @Override
        public boolean isEmpty() {
          return size() == 0;
        }

        @Override
        public boolean containsKey(Object key) {
          return applicationContext.containsBean(key.toString());
        }

        @Override
        public boolean containsValue(Object value) {
          return applicationContext.getBeansOfType(DataSource.class).containsValue(value);
        }

        @Override
        public Object get(Object key) {
          return applicationContext.getBean(key.toString());
        }

        @Override
        public Object put(Object key, Object value) {
          throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
          throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<?, ?> m) {
          throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public Set<Object> keySet() {
          return Collections.singleton(
              applicationContext.getBeansOfType(DataSource.class).keySet());
        }

        @Override
        public Collection<Object> values() {
          return Collections.singleton(
              applicationContext.getBeansOfType(DataSource.class).values());
        }

        @Override
        public Set<Entry<Object, Object>> entrySet() {
          return Collections.singleton(
              (Entry<Object, Object>)
                  applicationContext.getBeansOfType(DataSource.class).entrySet());
        }
      };

  public InSpringMultiDataSourceManager() {
    super(new HashMap<>());
  }

  @Override
  public DataSource getDataSource(String beanName) {
    return (DataSource) applicationContext.getBean(beanName);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public Map getAllDataSources() {
    return this.dataSourceMap;
  }

  @Override
  public void setApplicationContext(
      @SuppressWarnings("NullableProblems") ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }
}
