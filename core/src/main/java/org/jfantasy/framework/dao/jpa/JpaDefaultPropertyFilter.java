package org.jfantasy.framework.dao.jpa;

import java.util.Arrays;
import java.util.List;
import org.jfantasy.framework.dao.MatchType;
import org.springframework.data.jpa.domain.Specification;

/**
 * 属性过滤器
 *
 * @author limaofeng
 */
public class JpaDefaultPropertyFilter
    extends PropertyFilterBuilder<JpaDefaultPropertyFilter, List<PropertyPredicate>>
    implements WebPropertyFilter<Object> {

  private static final MatchType[] GENERAL_COMPARISON_CONDITION =
      new MatchType[] {
        MatchType.EQ,
        MatchType.NOT_EQUAL,
        MatchType.CONTAINS,
        MatchType.NOT_CONTAINS,
        MatchType.STARTS_WITH,
        MatchType.NOT_STARTS_WITH,
        MatchType.ENDS_WITH,
        MatchType.NOT_ENDS_WITH,
        MatchType.LT,
        MatchType.GT,
        MatchType.LTE,
        MatchType.GTE,
        MatchType.IN,
        MatchType.NOT_IN
      };

  private static final MatchType[] NOVALUE_COMPARISON_CONDITION =
      new MatchType[] {MatchType.NULL, MatchType.NOT_NULL, MatchType.EMPTY, MatchType.NOT_EMPTY};

  private static final JunctionPredicateCallback<List<PropertyPredicate>>
      DEFAULT_JUNCTION_PREDICATE_CALLBACK =
          (context, matchType, filters) -> {
            if (matchType == MatchType.AND
                || matchType == MatchType.OR
                || matchType == MatchType.NOT) {
              context.add(new PropertyPredicate(matchType, Arrays.asList(filters)));
            }
          };
  private static final PropertyPredicateCallback<List<PropertyPredicate>>
      DEFAULT_PROPERTY_PREDICATE_CALLBACK =
          (name, matchType, value, context) -> {
            if (matchType == MatchType.AND
                || matchType == MatchType.OR
                || matchType == MatchType.NOT) {
              context.add(new PropertyPredicate(matchType, value));
            } else if (matchType == MatchType.BETWEEN) {
              BetweenValue<?, ?> betweenValue = (BetweenValue<?, ?>) value;
              context.add(
                  new PropertyPredicate(
                      MatchType.BETWEEN, name, betweenValue.getX(), betweenValue.getY()));
            } else if (Arrays.stream(GENERAL_COMPARISON_CONDITION)
                .anyMatch(it -> it == matchType)) {
              context.add(new PropertyPredicate(matchType, name, value));
            } else if (Arrays.stream(NOVALUE_COMPARISON_CONDITION)
                .anyMatch(it -> it == matchType)) {
              context.add(new PropertyPredicate(matchType, name));
            }
          };

  public JpaDefaultPropertyFilter(Class<?> entityClass, List<PropertyPredicate> context) {
    super(entityClass, context);
    PropertyFilterBuilder.initDefaultConverters(entityClass);
    this.property(DEFAULT_PROPERTY_PREDICATE_CALLBACK);
    this.junction(MatchType.AND, DEFAULT_JUNCTION_PREDICATE_CALLBACK);
    this.junction(MatchType.OR, DEFAULT_JUNCTION_PREDICATE_CALLBACK);
    this.junction(MatchType.NOT, DEFAULT_JUNCTION_PREDICATE_CALLBACK);
    // TODO: 通过 entityClass 动态分配 property 与 junction，比如非字符串就不应该有 STARTS_WITH 条件
  }

  public JpaDefaultPropertyFilter(List<PropertyPredicate> context) {
    super(context);
    this.property(DEFAULT_PROPERTY_PREDICATE_CALLBACK);
    this.junction(MatchType.AND, DEFAULT_JUNCTION_PREDICATE_CALLBACK);
    this.junction(MatchType.OR, DEFAULT_JUNCTION_PREDICATE_CALLBACK);
    this.junction(MatchType.NOT, DEFAULT_JUNCTION_PREDICATE_CALLBACK);
  }

  public JpaDefaultPropertyFilter or(Specification<?>... specifications) {
    this.context.add(new PropertyPredicate(MatchType.OR, specifications));
    return this;
  }

  public JpaDefaultPropertyFilter and(Specification<?>... specifications) {
    this.context.add(new PropertyPredicate(MatchType.AND, Arrays.asList(specifications)));
    return this;
  }

  public JpaDefaultPropertyFilter not(Specification<?>... specifications) {
    this.context.add(new PropertyPredicate(MatchType.NOT, specifications));
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<PropertyPredicate> build() {
    return super.context;
  }
}
