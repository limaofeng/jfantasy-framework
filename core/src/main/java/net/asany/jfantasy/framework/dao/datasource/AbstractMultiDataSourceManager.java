package net.asany.jfantasy.framework.dao.datasource;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.function.Consumer;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

/**
 * 多数据源工厂
 *
 * @author limaofeng
 */
public class AbstractMultiDataSourceManager implements MultiDataSourceManager {

  private final Map<MultiDataSourceOperations, MultiDataSourceObservable> listeners =
      new HashMap<>();
  private final Map<String, DataSource> dataSourceMap;

  public AbstractMultiDataSourceManager(Map<String, DataSource> dataSourceMap) {
    for (MultiDataSourceOperations operation : MultiDataSourceOperations.values()) {
      listeners.put(operation, new MultiDataSourceObservable());
    }
    this.dataSourceMap = dataSourceMap;
  }

  @Override
  public DataSource getDataSource(String dataSourceKey) {
    return dataSourceMap.get(dataSourceKey);
  }

  @Override
  public Map<String, DataSource> getAllDataSources() {
    return this.dataSourceMap;
  }

  @Override
  public void addDataSource(String name, DataSource dataSource) {
    MultiDataSourceObservable observable = listeners.get(MultiDataSourceOperations.ADD_DATA_SOURCE);
    if (this.dataSourceMap.containsKey(name)) {
      // TODO: destroy datasource
      this.dataSourceMap.get(name);
      return;
    }
    this.dataSourceMap.put(name, dataSource);
    observable.invoke(MultiDataSourceEvent.builder().name(name).dataSource(dataSource).build());
  }

  @Override
  public void addDataSource(DataSourceProperties properties) {
    DataSource dataSource =
        properties.initializeDataSourceBuilder().type(properties.getType()).build();
    this.addDataSource(properties.getName(), dataSource);
  }

  @Override
  public void removeDataSource(String name) {
    DataSource dataSource = this.dataSourceMap.remove(name);
    if (dataSource != null) {
      MultiDataSourceObservable observable =
          listeners.get(MultiDataSourceOperations.REMOVE_DATA_SOURCE);
      observable.invoke(MultiDataSourceEvent.builder().name(name).dataSource(dataSource).build());
    }
  }

  @Override
  public void on(MultiDataSourceOperations eventName, Consumer<MultiDataSourceEvent> callback) {
    MultiDataSourceObservable observable = listeners.get(eventName);
    observable.addObserver((o, arg) -> callback.accept((MultiDataSourceEvent) arg));
  }

  static class MultiDataSourceObservable extends Observable {
    public void invoke(MultiDataSourceEvent event) {
      setChanged();
      notifyObservers(event);
    }
  }
}
