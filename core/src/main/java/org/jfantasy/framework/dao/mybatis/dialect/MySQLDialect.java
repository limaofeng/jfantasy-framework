package org.jfantasy.framework.dao.mybatis.dialect;

/**
 * MySql 翻页方言
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-14 下午02:12:16
 */
public class MySQLDialect implements Dialect {

  private static final String SQL_END_DELIMITER = ";";

  @Override
  public String getLimitString(String osql, int offset, int limit) {
    String sql = trim(osql);
    StringBuilder sb = new StringBuilder(sql.length() + 20);
    sb.append(sql);
    if (offset > 0) {
      sb.append(" limit ").append(offset).append(',').append(limit).append(SQL_END_DELIMITER);
    } else {
      sb.append(" limit ").append(limit).append(SQL_END_DELIMITER);
    }
    return sb.toString();
  }

  private String trim(String osql) {
    String sql = osql.trim();
    if (sql.endsWith(";")) {
      sql = sql.substring(0, sql.length() - 1 - ";".length());
    }
    return sql;
  }

  @Override
  public String getCountString(String sql) {
    return DialectUtil.pretty("select count(1) from (" + trim(sql) + ") as countSql");
  }
}
