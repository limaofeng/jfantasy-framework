package net.asany.jfantasy.framework.dao.datasource.sharding;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * 分片数据源代理
 *
 * @author limaofeng
 */
public class ShardingDataSourceProxy implements DataSource {

  private final ShardingConfiguration shardingConfiguration;

  public ShardingDataSourceProxy(ShardingConfiguration shardingConfiguration) {
    this.shardingConfiguration = shardingConfiguration;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return shardingConfiguration.getDataSource().getConnection();
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return shardingConfiguration.getDataSource().getConnection(username, password);
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return shardingConfiguration.getDataSource().unwrap(iface);
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return shardingConfiguration.getDataSource().isWrapperFor(iface);
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return shardingConfiguration.getDataSource().getLogWriter();
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    shardingConfiguration.getDataSource().setLogWriter(out);
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    shardingConfiguration.getDataSource().setLoginTimeout(seconds);
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return shardingConfiguration.getDataSource().getLoginTimeout();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return shardingConfiguration.getDataSource().getParentLogger();
  }

  public DataSource getPrimaryDataSource() {
    return shardingConfiguration.getPrimaryDataSource();
  }
}
