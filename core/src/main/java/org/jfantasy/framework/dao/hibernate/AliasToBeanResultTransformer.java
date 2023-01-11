package org.jfantasy.framework.dao.hibernate;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import jakarta.persistence.Column;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.hibernate.util.TypeFactory;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;

/**
 * 返回结果集转换器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-12 上午9:52:00
 */
@Slf4j
public class AliasToBeanResultTransformer implements ResultTransformer {
  private static final long serialVersionUID = -5199190581393587893L;

  private final Class<?> resultClass;

  private final Map<String, String> propertyNames = new HashMap<>();
  private final Map<String, Type> propertyTypes = new HashMap<>();

  public AliasToBeanResultTransformer(Class<?> resultClass) {
    if (resultClass == null) {
      throw new IllegalArgumentException("resultClass cannot be null");
    }
    this.resultClass = resultClass;
    Field[] fields = ClassUtil.getDeclaredFields(resultClass, Column.class);
    for (Field field : fields) {
      Column column = ClassUtil.getFieldGenericType(field, Column.class);
      propertyNames.put(column.name(), field.getName());
      Type type = getHibernateType(field.getType());
      if (type != null) {
        propertyTypes.put(column.name(), type);
      }
    }
  }

  public Type getHibernateType(Class<?> type) {
    return TypeFactory.basic(type.getName());
  }

  @Override
  public Object transformTuple(Object[] tuple, String[] aliases) {
    Object result = ClassUtil.newInstance(this.resultClass);
    for (int i = 0; i < aliases.length; i++) {
      OgnlUtil.getInstance().setValue(convertColumnToProperty(aliases[i]), result, tuple[i]);
    }
    return result;
  }

  public String convertColumnToProperty(String columnName) {
    if (this.propertyNames.containsKey(columnName)) {
      return propertyNames.get(columnName);
    }
    columnName = columnName.toLowerCase();
    StringBuilder buff = new StringBuilder(columnName.length());
    StringTokenizer st = new StringTokenizer(columnName, "_");
    while (st.hasMoreTokens()) {
      buff.append(StringUtils.capitalize(st.nextToken()));
    }
    buff.setCharAt(0, Character.toLowerCase(buff.charAt(0)));
    return buff.toString();
  }

  @Override
  @SuppressWarnings("rawtypes")
  public List transformList(List collection) {
    return collection;
  }

  //  protected void custom(SQLQuery query) {
  //    for (Map.Entry<String, Type> entry : propertyTypes.entrySet()) {
  //      query.addScalar(entry.getKey(), entry.getValue());
  //    }
  //  }
}
