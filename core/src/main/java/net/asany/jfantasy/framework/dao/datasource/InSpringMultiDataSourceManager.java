package net.asany.jfantasy.framework.dao.datasource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

  private final Map<String, DataSource> dataSourceMap =
      new Map<>() {
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
        public DataSource get(Object key) {
          return (DataSource) applicationContext.getBean(key.toString());
        }

        @Nullable
        @Override
        public DataSource put(String key, DataSource value) {
          throw new UnsupportedOperationException();
        }

        @Override
        public DataSource remove(Object key) {
          throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(@NotNull Map<? extends String, ? extends DataSource> m) {
          throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
          throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull Set<String> keySet() {
          return Stream.of(applicationContext.getBeansOfType(DataSource.class).keySet())
              .map(Object::toString)
              .collect(Collectors.toSet());
        }

        @Override
        public @NotNull Collection<DataSource> values() {
          return new ArrayList<>(applicationContext.getBeansOfType(DataSource.class).values());
        }

        @Override
        public @NotNull Set<Entry<String, DataSource>> entrySet() {
          return applicationContext.getBeansOfType(DataSource.class).entrySet().stream()
              .map(
                  entry ->
                      new Entry<String, DataSource>() {
                        @Override
                        public String getKey() {
                          return entry.getKey();
                        }

                        @Override
                        public DataSource getValue() {
                          return entry.getValue();
                        }

                        @Override
                        public DataSource setValue(DataSource value) {
                          throw new UnsupportedOperationException();
                        }
                      })
              .collect(Collectors.toSet());
        }
      };

  public InSpringMultiDataSourceManager() {
    super(new HashMap<>());
  }

  @Override
  public DataSource getDataSource(String beanName) {
    return (DataSource) applicationContext.getBean(beanName);
  }

  @Override
  public Map<String, DataSource> getAllDataSources() {
    return this.dataSourceMap;
  }

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }
}
