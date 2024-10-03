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
import java.util.Arrays;
import java.util.Objects;
import net.asany.jfantasy.framework.dao.MatchType;
import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import net.asany.jfantasy.framework.dao.jpa.PropertyFilter;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * Input Filter 查询根类
 *
 * @author limaofeng
 * @version V1.0
 * @date 2020/4/14 10:07 上午
 */
public abstract class WhereInput<F extends WhereInput<?, ?>, T> {

  protected final Class<T> entityClass;
  protected static String[] MULTI_VALUE = new String[] {"in", "notIn"};

  protected PropertyFilter filter;

  public WhereInput() {
    this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass(), 1);
    this.filter = initPropertyFilter();
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

  @JsonAnySetter
  public void set(String name, Object value) {
    String[] slugs = StringUtil.tokenizeToStringArray(name, "_");
    Object newValue;
    if (slugs.length > 1 && ObjectUtil.exists(MULTI_VALUE, slugs[1])) {
      newValue = Arrays.stream(multipleValuesObjectsObjects(value)).toArray(Object[]::new);
    } else {
      newValue = value;
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
