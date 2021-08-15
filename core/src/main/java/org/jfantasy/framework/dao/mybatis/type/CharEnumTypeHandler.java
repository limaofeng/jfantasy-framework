package org.jfantasy.framework.dao.mybatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 枚举转字符的Handler
 *
 * @param <E>
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-29 下午09:04:53
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CharEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

  protected Class<E> enumClass;
  private Enum[] enums = new Enum[0];

  public CharEnumTypeHandler() {
    this.enumClass = ClassUtil.getSuperClassGenricType(getClass());
    this.enums = (Enum[]) ClassUtil.getMethodProxy(this.enumClass, "values").invoke(null);
    ConvertUtils.register(
        new Converter() {

          @Override
          public Object convert(Class classes, Object value) {
            if (value.getClass().isAssignableFrom(enumClass)) {
              E e = (E) value;
              return String.valueOf(e.ordinal());
            } else {
              return enums[Integer.valueOf((String) value)];
            }
          }
        },
        this.enumClass);
  }

  public CharEnumTypeHandler(Converter converter) {
    this.enumClass = ClassUtil.getSuperClassGenricType(getClass());
    ConvertUtils.register(converter, this.enumClass);
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
      throws SQLException {
    ps.setString(i, StringUtil.nullValue(ConvertUtils.convert(parameter, enumClass)));
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    String columnValue = rs.getString(columnName);
    if (rs.wasNull()) {
      return null;
    }
    try {
      return (E) ConvertUtils.convert(columnValue, enumClass);
    } catch (Exception ex) {
      throw new IllegalArgumentException(
          "Cannot convert "
              + columnValue
              + " to "
              + this.enumClass.getSimpleName()
              + " by ordinal value.",
          ex);
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    String columnValue = rs.getString(columnIndex);
    if (rs.wasNull()) {
      return null;
    }
    try {
      return (E) ConvertUtils.convert(columnValue, enumClass);
    } catch (Exception ex) {
      throw new IllegalArgumentException(
          "Cannot convert "
              + columnValue
              + " to "
              + this.enumClass.getSimpleName()
              + " by ordinal value.",
          ex);
    }
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    String columnValue = cs.getString(columnIndex);
    if (cs.wasNull()) {
      return null;
    }
    try {
      return (E) ConvertUtils.convert(columnValue, enumClass);
    } catch (Exception ex) {
      throw new IllegalArgumentException(
          "Cannot convert "
              + columnValue
              + " to "
              + this.enumClass.getSimpleName()
              + " by ordinal value.",
          ex);
    }
  }
}
