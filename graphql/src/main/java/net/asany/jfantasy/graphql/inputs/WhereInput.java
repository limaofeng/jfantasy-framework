/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.graphql.inputs;

import static net.asany.jfantasy.framework.util.common.ObjectUtil.multipleValuesObjectsObjects;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import net.asany.jfantasy.framework.dao.MatchType;
import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import net.asany.jfantasy.framework.dao.jpa.PropertyFilter;
import net.asany.jfantasy.framework.util.common.BeanUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.error.UnsupportedException;
import net.asany.jfantasy.graphql.util.GraphQLUtils;

/**
 * Input Filter 查询根类
 *
 * @author limaofeng
 * @version V1.0
 * @since 2021-07-17 10:00
 */
public abstract class WhereInput<F extends WhereInput<?, ?>, T> {

  protected final Class<T> entityClass;
  protected static String RANGE = "range";
  protected static String[] MULTI_VALUE = new String[] {"in", "notIn", "between"};

  protected PropertyFilter filter;
  protected String typeName;

  public WhereInput() {
    this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass(), 1);
    this.filter = initPropertyFilter();
    this.typeName = this.getClass().getSimpleName();
  }

  public WhereInput(String graphQLType) {
    this();
    this.typeName = graphQLType;
  }

  protected PropertyFilter initPropertyFilter() {
    return PropertyFilter.newFilter(this.entityClass);
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

  public Object rangeValue(GraphQLInputObjectField field, Map<?, ?> map, Object value) {
    if (field.getType() instanceof GraphQLInputObjectType objectType) {
      if (DateRange.class.getSimpleName().equals(objectType.getName())) {
        return DateRange.builder()
            .start(BeanUtil.convert(map.get("start"), Date.class))
            .end(BeanUtil.convert(map.get("end"), Date.class))
            .build();
      } else if (IntRange.class.getSimpleName().equals(objectType.getName())) {
        return IntRange.builder()
            .min(BeanUtil.convert(map.get("min"), Integer.class))
            .max(BeanUtil.convert(map.get("max"), Integer.class))
            .build();
      } else if (FloatRange.class.getSimpleName().equals(objectType.getName())) {
        return FloatRange.builder()
            .min(BeanUtil.convert(map.get("min"), Float.class))
            .max(BeanUtil.convert(map.get("max"), Float.class))
            .build();
      }
    }
    throw new UnsupportedException("Unsupported Range Type : " + value.getClass().getName());
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    String[] slugs = StringUtil.tokenizeToStringArray(name, "_");
    if (slugs.length == 1) {
      filter.equal(name, value);
      return;
    }
    String queryName = slugs[0];
    String matchType = slugs[1];
    if (RANGE.equals(matchType)) {
      //noinspection rawtypes
      if (value instanceof Range range) {
        //noinspection unchecked
        filter.between(queryName, range.getMin(), range.getMax());
      } else if (value instanceof DateRange dateRange) {
        filter.between(queryName, dateRange.getStart(), dateRange.getEnd());
      } else if (value instanceof Map<?, ?> map) {
        GraphQLInputObjectField field = GraphQLUtils.getFieldDefinition(this.typeName, name);
        if (field == null) {
          throw new UnsupportedException("Unsupported Range Type : " + value.getClass().getName());
        }
        this.set(name, rangeValue(field, map, value));
      } else {
        throw new UnsupportedException("Unsupported Range Type : " + value.getClass().getName());
      }
      return;
    }
    if (ObjectUtil.exists(MULTI_VALUE, matchType)) {
      value = Arrays.stream(multipleValuesObjectsObjects(value)).toArray(Object[]::new);
    }
    Objects.requireNonNull(MatchType.get(matchType)).build(this.filter, queryName, value);
  }

  public <P extends PropertyFilter> P toFilter() {
    //noinspection unchecked
    return (P) this.filter;
  }
}
