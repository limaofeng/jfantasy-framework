package net.asany.jfantasy.framework.dao.util;

import java.sql.*;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.ValidationException;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * JDBC 工具类
 *
 * @author limaofeng@msn.com
 */
@Slf4j
public abstract class JdbcUtils extends org.springframework.jdbc.support.JdbcUtils {

  @SneakyThrows
  public static void dropColumn(String tableName, String columnName) {
    DataSource dataSource = SpringBeanUtils.getBeanByType(DataSource.class);
    Connection connection = null;
    Statement stmt = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();
      //noinspection SqlSourceToSinkFlow
      int i =
          stmt.executeUpdate(
              String.format("alter TABLE %s drop column %s;", tableName, columnName));
      log.debug("dropColumn {} {}. Number of affected rows: {}", tableName, columnName, i);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      closeStatement(stmt);
      closeConnection(connection);
    }
  }

  @SneakyThrows
  public static void renameColumn(String tableName, String oldColumnName, String newColumnName) {
    DataSource dataSource = SpringBeanUtils.getBeanByType(DataSource.class);
    Connection connection = dataSource.getConnection();
    PreparedStatement ps = null;
    ResultSet rs = null;
    String columnType;
    try {
      ps =
          connection.prepareStatement(
              "select column_type from information_schema.columns where table_name = ? and column_name = ?");
      ps.setString(1, tableName);
      ps.setString(2, oldColumnName);
      rs = ps.executeQuery();
      columnType = rs.next() ? rs.getString(1) : null;
      if (columnType == null) {
        throw new ValidationException("未发现字段");
      }
    } finally {
      closeStatement(ps);
      closeResultSet(rs);
    }
    try {
      //noinspection SqlSourceToSinkFlow
      ps =
          connection.prepareStatement(
              String.format(
                  "alter table %s change %s %s %s",
                  tableName, oldColumnName, newColumnName.toLowerCase(), columnType));
      ps.executeUpdate();
    } finally {
      closeStatement(ps);
      closeConnection(connection);
    }
  }

  public static void renameTable(String tableName, String newTableName) {
    DataSource dataSource = SpringBeanUtils.getBeanByType(DataSource.class);
    Connection connection = null;
    Statement stmt = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();
      //noinspection SqlSourceToSinkFlow
      stmt.executeUpdate(
          String.format("alter table %s rename to %s", tableName, newTableName.toLowerCase()));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      closeStatement(stmt);
      closeConnection(connection);
    }
  }

  public static void dropTable(String tableName) {
    DataSource dataSource = SpringBeanUtils.getBeanByType(DataSource.class);
    Connection connection = null;
    Statement stmt = null;
    try {
      connection = dataSource.getConnection();
      stmt = connection.createStatement();
      //noinspection SqlSourceToSinkFlow
      stmt.executeUpdate(String.format("drop table %s;", tableName));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      closeStatement(stmt);
      closeConnection(connection);
    }
  }

  public static void rollback(Connection con) {
    try {
      if (con != null) {
        con.rollback();
      }
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static <T extends Exception> void rollback(Connection con, T re) throws T {
    try {
      con.rollback();
    } catch (SQLException e) {
      log.error(e.getMessage(), e);
      throw re;
    }
  }

  public static Transaction transaction() {
    PlatformTransactionManager transactionManager =
        SpringBeanUtils.getBean("transactionManager", PlatformTransactionManager.class);
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
    assert transactionManager != null;
    return new Transaction(transactionManager, transactionManager.getTransaction(def));
  }

  public static Transaction transaction(int propagationBehavior) {
    PlatformTransactionManager transactionManager =
        SpringBeanUtils.getBean("transactionManager", PlatformTransactionManager.class);
    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
    def.setPropagationBehavior(propagationBehavior);
    assert transactionManager != null;
    return new Transaction(transactionManager, transactionManager.getTransaction(def));
  }

  public static <T> T transaction(Callback<T> callback) {
    Transaction transaction = transaction();
    try {
      T val = callback.run();
      if (val instanceof HibernateProxy) {
        Hibernate.initialize(val);
      }
      return val;
    } finally {
      transaction.commit();
    }
  }

  public static <T> T transaction(Callback<T> callback, int propagationBehavior) {
    Transaction transaction = transaction(propagationBehavior);
    try {
      T val = callback.run();
      if (val instanceof HibernateProxy) {
        Hibernate.initialize(val);
      }
      return val;
    } finally {
      transaction.commit();
    }
  }

  public interface Callback<T> {

    T run();
  }

  public static class Transaction {

    private final PlatformTransactionManager transactionManager;
    private final TransactionStatus status;

    public Transaction(PlatformTransactionManager transactionManager, TransactionStatus status) {
      this.transactionManager = transactionManager;
      this.status = status;
    }

    public void commit() {
      transactionManager.commit(status);
    }
  }
}
