package org.jfantasy.framework.dao.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

/**
 * 多数据源
 *
 * @author limaofeng
 */
@Slf4j
public class MultiDataSource extends AbstractRoutingDataSource {

  private CatalogConverter catalogConverter;

  private final MultiDataSourceManager dataSourceManager;

  public MultiDataSource(DataSource primaryDataSource, MultiDataSourceManager dataSourceManager) {
    this(primaryDataSource, dataSourceManager, null);
  }

  public MultiDataSource(
      DataSource primaryDataSource,
      MultiDataSourceManager dataSourceManager,
      CatalogConverter catalogConverter) {
    super.setDefaultTargetDataSource(primaryDataSource);
    super.setTargetDataSources(new HashMap<>(dataSourceManager.getAllDataSources()));
    this.dataSourceManager = dataSourceManager;
    if (ObjectUtil.isNull(catalogConverter)) {
      this.catalogConverter = new SimpleCatalogConverter();
    }
  }

  @NotNull
  @Override
  public Connection getConnection() throws SQLException {
    Connection connection = super.getConnection();
    String catalog = getCatalog();
    if (ObjectUtil.isNull(DataSourceContextHolder.getDataSource())) {
      log.error("没有匹配到 DataSource:" + connection);
    }
    if (StringUtil.isNotBlank(catalog)) {
      setCatalog(connection, catalog);
    }
    return connection;
  }

  private void setCatalog(Connection connection, String catalog) {
    try {
      connection.setCatalog(catalogConverter.convert(catalog));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public String getCatalog() {
    DataSourceRouteProperties dataSource = DataSourceContextHolder.getDataSource();
    if ((ObjectUtil.isNull(dataSource)) || (StringUtil.isBlank(dataSource.getCatalog()))) {
      return null;
    }
    return dataSource.getCatalog();
  }

  @Override
  protected Object determineCurrentLookupKey() {
    DataSourceRouteProperties properties = DataSourceContextHolder.getDataSource();
    if (ObjectUtil.isNull(properties)) {
      log.error("没有匹配到 DataSource, 将使用默认数据源!");
      return null;
    }
    String dataSourceKey = properties.getName();
    if (!containsDataSource(dataSourceKey)) {
      throw new DataSourceLookupFailureException("没有匹配到 DataSource:" + dataSourceKey);
    }
    if (this.dataSourceManager != null) {
      DataSource dataSource = this.dataSourceManager.getDataSource(dataSourceKey);
      this.addDataSource(dataSourceKey, dataSource);
    }
    return dataSourceKey;
  }

  public void addDataSource(String key, DataSource dataSource) {
    Map<Object, DataSource> resolvedDataSources = ClassUtil.getValue(this, "resolvedDataSources");
    Object lookupKey = resolveSpecifiedLookupKey(key);
    resolvedDataSources.put(lookupKey, dataSource);
  }

  private boolean containsDataSource(String key) {
    Map<Object, DataSource> resolvedDataSources = ClassUtil.getValue(this, "resolvedDataSources");
    return resolvedDataSources.containsKey(resolveSpecifiedLookupKey(key));
  }

  /** 数据库名称转换器 */
  public interface CatalogConverter {

    /**
     * 转换数据库名称
     *
     * @param catalog 配置的 数据库名称
     * @return 实际的数据库名称
     */
    String convert(String catalog);
  }

  public static class SimpleCatalogConverter implements CatalogConverter {

    @Override
    public String convert(String catalog) {
      return catalog;
    }
  }
}
