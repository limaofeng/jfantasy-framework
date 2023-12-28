package net.asany.jfantasy.framework.dao.mybatis.dialect;

/**
 * MyBatis 方言接口
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:22:02
 */
public interface Dialect {

  String getLimitString(String sql, int offset, int limit);

  String getCountString(String sql);
}
