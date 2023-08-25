package org.jfantasy.framework.dao.jpa;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jfantasy.framework.dao.MatchType;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

/**
 * 通用过滤器
 *
 * @author limaofeng
 */
public class PropertyPredicate {

  /** 完整表达式 */
  private String key;
  /** 名称 */
  private String propertyName;
  /** 值 */
  private Object propertyValue;
  /** 过滤类型 */
  private MatchType matchType;

  public <T> PropertyPredicate(MatchType matchType, T value) {
    this.matchType = matchType;
    this.propertyValue = value;
  }

  @Deprecated
  public <T> PropertyPredicate(String filterName, T value) {
    String errorTemplate = "filter名称 %s 没有按规则编写,无法得到属性比较类型.";
    String matchTypeStr = StringUtils.substringBefore(filterName, "_");
    this.matchType = MatchType.get(matchTypeStr);
    Assert.notNull(this.matchType, String.format(errorTemplate, filterName));
    this.propertyName = StringUtils.substringAfter(filterName, "_");
    this.propertyValue = value;
  }

  @Deprecated
  public <T> PropertyPredicate(String filterName) {
    String errorTemplate = "filter名称 %s 没有按规则编写,无法得到属性比较类型.";
    String matchTypeStr = StringUtils.substringBefore(filterName, "_");
    this.matchType = MatchType.get(matchTypeStr);
    Assert.notNull(this.matchType, String.format(errorTemplate, filterName));
    this.propertyName = StringUtils.substringAfter(filterName, "_");
  }

  @SafeVarargs
  @Deprecated
  public <T> PropertyPredicate(String filterName, T... value) {
    String errorTemplate = "filter名称 %s 没有按规则编写,无法得到属性比较类型.";
    String matchTypeStr = StringUtils.substringBefore(filterName, "_");
    this.matchType = MatchType.get(matchTypeStr);
    Assert.notNull(this.matchType, String.format(errorTemplate, filterName));
    this.propertyName = StringUtils.substringAfter(filterName, "_");
    this.propertyValue = value;
  }

  public <T> PropertyPredicate(MatchType matchType, String propertyName) {
    this.initialize(matchType, propertyName);
  }

  public <T> PropertyPredicate(MatchType matchType, String propertyName, T value) {
    this.initialize(matchType, propertyName);
    this.propertyValue = value;
  }

  @SafeVarargs
  public <T> PropertyPredicate(MatchType matchType, String propertyName, T... value) {
    this.initialize(matchType, propertyName);
    this.propertyValue = value;
  }

  private void initialize(MatchType matchType, String propertyName) {
    this.matchType = matchType;
    this.propertyName = propertyName;
  }

  public String getKey() {
    return this.matchType + "_" + propertyName;
  }

  public String getPropertyName() {
    return this.propertyName;
  }

  public <T> T getPropertyValue() {
    return (T) this.propertyValue;
  }

  public <T> T getPropertyValue(Class<T> clazz) {
    if (this.isPropertyFilter() && MatchType.isLogicOperator(this.matchType)) {
      List<PropertyFilter> filters = (List<PropertyFilter>) this.propertyValue;
      return (T) filters.stream().map(PropertyFilter::build).collect(Collectors.toList());
    }
    return (T) this.propertyValue;
  }

  public String getFilterName() {
    return this.matchType + "_" + this.propertyName;
  }

  public boolean isPropertyFilter() {
    if (this.propertyValue instanceof PropertyFilter) {
      return true;
    }
    if (!MatchType.isLogicOperator(this.matchType)) {
      return false;
    }
    if (!ClassUtil.isList(this.propertyValue) || ((List<?>) this.propertyValue).isEmpty()) {
      return false;
    }
    return ((List<?>) this.propertyValue).get(0) instanceof PropertyFilter;
  }

  public boolean isSpecification() {
    return this.propertyValue instanceof Specification;
  }

  public boolean isExpression() {
    return isPropertyFilter() || isSpecification();
  }

  public MatchType getMatchType() {
    return this.matchType;
  }

  @Override
  public String toString() {
    return "PropertyFilter [matchType="
        + matchType
        + ", propertyName="
        + propertyName
        + ", propertyValue="
        + JSON.serialize(propertyValue)
        + "]";
  }
}
