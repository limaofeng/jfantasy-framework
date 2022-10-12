package org.jfantasy.framework.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
public class MultiDataSource extends AbstractRoutingDataSource {

  private CatalogConverter catalogConverter;

  @Override
  public void afterPropertiesSet() {
    super.afterPropertiesSet();
    if (ObjectUtil.isNull(catalogConverter)) {
      catalogConverter = new SimpleCatalogConverter();
    }
  }

  @Override
  public Connection getConnection() throws SQLException {
    Connection connection = super.getConnection();
    String catalog = getCatalog();
    if (ObjectUtil.isNull(getDataSource())) {
      log.error("没有匹配到DataSource:" + connection);
    }
    if (StringUtil.isNotBlank(catalog)) {
      setCatalog(connection, catalog);
    }
    return connection;
  }

  private org.jfantasy.framework.dao.annotations.DataSource getDataSource() {
    return MultiDataSourceManager.getManager().peek();
  }

  private void setCatalog(Connection connection, String catalog) {
    try {
      connection.setCatalog(catalogConverter.convert(catalog));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public String getCatalog() {
    org.jfantasy.framework.dao.annotations.DataSource dataSource = getDataSource();
    if ((ObjectUtil.isNull(dataSource)) || (StringUtil.isBlank(dataSource.catalog()))) {
      return null;
    }
    return dataSource.catalog();
  }

  @Override
  protected Object determineCurrentLookupKey() {
    org.jfantasy.framework.dao.annotations.DataSource dataSource = getDataSource();
    if (ObjectUtil.isNull(dataSource)) {
      log.error("没有匹配到DataSource,将使用默认数据源!");
      return null;
    }
    if (containsDataSource(dataSource.name())) {
      return dataSource.name();
    }
    try {
      DataSource object = (DataSource) SpringBeanUtils.getBean(dataSource.name());
      Map<Object, DataSource> resolvedDataSources =
          (Map<Object, DataSource>) ClassUtil.getValue(this, "resolvedDataSources");
      Object lookupKey = resolveSpecifiedLookupKey(dataSource.name());
      resolvedDataSources.put(lookupKey, object);
      return dataSource.name();
    } catch (BeansException e) {
      log.error("没有匹配到DataSource,将使用默认数据源!", e);
    }
    return null;
  }

  private boolean containsDataSource(String key) {
    Map<Object, DataSource> resolvedDataSources = ClassUtil.getValue(this, "resolvedDataSources");
    return resolvedDataSources.containsKey(resolveSpecifiedLookupKey(key));
  }

  public void setCatalogConverter(CatalogConverter catalogConverter) {
    this.catalogConverter = catalogConverter;
  }

  public static interface CatalogConverter {

    public String convert(String catalog);
  }

  public static class SimpleCatalogConverter implements CatalogConverter {

    @Override
    public String convert(String catalog) {
      return catalog;
    }
  }
}
