package net.asany.jfantasy.framework.dao.datasource;

import java.util.HashMap;
import org.junit.jupiter.api.Test;

class AbstractMultiDataSourceFactoryTest {

  @Test
  void addDataSource() {
    AbstractMultiDataSourceManager observable = new AbstractMultiDataSourceManager(new HashMap<>());

    observable.on(
        MultiDataSourceOperations.ADD_DATA_SOURCE, (event) -> System.out.println(event.toString()));
    observable.on(
        MultiDataSourceOperations.ADD_DATA_SOURCE, (event) -> System.out.println(event.toString()));

    observable.addDataSource("xx", null);
    observable.addDataSource("2222", null);
  }
}
