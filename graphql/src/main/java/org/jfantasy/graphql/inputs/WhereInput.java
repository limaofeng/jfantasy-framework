package org.jfantasy.graphql.inputs;

import static org.jfantasy.framework.util.common.ObjectUtil.multipleValuesObjectsObjects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jfantasy.framework.dao.MatchType;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * Input Filter 查询根类
 *
 * @author limaofeng
 * @version V1.0
 * @date 2020/4/14 10:07 上午
 */
public abstract class WhereInput<F extends WhereInput<?, ?>, T> {

  protected final Class<T> entityClass;
  private final Map<String, TypeConverter<?>> fields = new HashMap<>();

  protected static String[] MULTI_VALUE = new String[] {"in", "notIn"};

  protected PropertyFilter filter = initPropertyFilter();

  public WhereInput() {
    this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass(), 1);
    if (this.entityClass == Object.class) {
      return;
    }
    for (Field field : ClassUtil.getDeclaredFields(this.entityClass)) {
      if (ClassUtil.isBasicType(field.getType())) {
        fields.put(field.getName(), new DefaultTypeConverter<>(field.getType()));
      }
    }
  }

  protected void register(String name, TypeConverter<?> converter) {
    this.fields.put(name, converter);
  }

  protected PropertyFilter initPropertyFilter() {
    return PropertyFilter.newFilter();
  }

  @JsonProperty("AND")
  public void setAnd(F[] filters) {
    filter.and(Arrays.stream(filters).map(item -> item.filter).toArray(PropertyFilter[]::new));
  }

  @JsonProperty("OR")
  public void setOr(F[] filters) {
    filter.or(Arrays.stream(filters).map(item -> item.filter).toArray(PropertyFilter[]::new));
  }

  @JsonProperty("NOT")
  public void setNot(F[] filters) {
    filter.not(Arrays.stream(filters).map(item -> item.filter).toArray(PropertyFilter[]::new));
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    String[] slugs = StringUtil.tokenizeToStringArray(name, "_");
    Object newValue;
    if (slugs.length > 1 && ObjectUtil.exists(MULTI_VALUE, slugs[1])) {
      newValue =
          Arrays.stream(multipleValuesObjectsObjects(value))
              .map(item -> this.fields.get(slugs[0]).convert(item))
              .toArray(Object[]::new);
    } else {
      newValue = this.fields.get(slugs[0]).convert(value);
    }
    if (slugs.length == 1) {
      filter.equal(name, newValue);
      return;
    }
    Objects.requireNonNull(MatchType.get(slugs[1])).build(this.filter, slugs[0], newValue);
  }

  public <P extends PropertyFilter> P toFilter() {
    return (P) this.filter;
  }
}
