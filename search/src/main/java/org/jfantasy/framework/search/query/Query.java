package org.jfantasy.framework.search.query;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.ObjectBuilder;
import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jfantasy.framework.search.exception.SearchException;
import org.jfantasy.framework.util.common.DateUtil;
import org.springframework.util.Assert;

public class Query extends QueryBase {

  private final ObjectBuilder<QueryVariant> builder;

  private Query(ObjectBuilder builder) {
    super((AbstractBuilder<?>) builder);
    this.builder = builder;
  }

  public static <T> Query of(ObjectBuilder<T> builder) {
    return new Query(builder);
  }

  /**
   * 单个字段匹配
   *
   * @param field 字段
   * @param value 匹配值
   * @return Query
   */
  public static Query term(String field, String value) {
    return Query.of(QueryBuilders.term().field(field).value(value));
  }

  /**
   * 单个字段匹配
   *
   * @param field 字段
   * @param value 匹配值
   * @return Query
   */
  public static Query term(String field, long value) {
    return Query.of(QueryBuilders.term().field(field).value(value));
  }

  /**
   * 单个字段匹配
   *
   * @param field 字段
   * @param value 匹配值
   * @return Query
   */
  public static Query term(String field, double value) {
    return Query.of(QueryBuilders.term().field(field).value(value));
  }

  /**
   * 单个字段匹配
   *
   * @param field 字段
   * @param value 匹配值
   * @return Query
   */
  public static Query term(String field, boolean value) {
    return Query.of(QueryBuilders.term().field(field).value(value));
  }

  /**
   * 通配符查询
   *
   * @param field 字段
   * @param value 通配符
   * @return Query
   */
  public static Query wildcard(String field, String value) {
    return new Query(QueryBuilders.wildcard().field(field).value(value));
  }

  /**
   * 通配符查询 (多个条件用 OR 连接)
   *
   * @param fields 多字段
   * @param value 通配符
   * @return Query
   */
  public static Query wildcard(String[] fields, String value) {
    Assert.isTrue(fields.length != 0, "param fields length must be greater than 1 ");
    Query[] wildcardQueries = new Query[fields.length];
    for (int i = 0; i < fields.length; i++) {
      wildcardQueries[i] = new Query(QueryBuilders.wildcard().field(fields[i]).value(value));
    }
    return should(wildcardQueries);
  }

  /**
   * 通配符查询
   *
   * @param fields 多字段
   * @param value 通配符
   * @param clause 条件连接方式
   * @return Query
   */
  public static Query wildcard(String[] fields, String value, BooleanClause clause) {
    Assert.isTrue(fields.length != 0, "param fields length must be greater than 1 ");
    Query[] wildcardQueries = new Query[fields.length];
    for (int i = 0; i < fields.length; i++) {
      wildcardQueries[i] = new Query(QueryBuilders.wildcard().field(fields[i]).value(value));
    }
    return combine(wildcardQueries, clause);
  }

  /**
   * 单个字段匹配 - 匹配开始
   *
   * @param field 匹配字段
   * @param value 匹配值
   * @return Query
   */
  public static Query prefix(String field, String value) {
    return Query.of(QueryBuilders.prefix().field(field).value(value));
  }

  public static Query matchAll(Function<MatchAllQuery.Builder, MatchAllQuery.Builder> fn) {
    return Query.of(fn.apply(QueryBuilders.matchAll()));
  }

  /**
   * 单个字段模糊检索
   *
   * @param field 字段
   * @param value 比较值
   * @return Query
   */
  public static Query match(String field, String value) {
    return Query.of(QueryBuilders.match().field(field).query(value));
  }

  /**
   * 单个字段模糊检索
   *
   * @param field 字段
   * @param value 比较值
   * @param analyzer 分析器
   * @return Query
   */
  public static Query match(String field, String value, String analyzer) {
    return Query.of(QueryBuilders.match().field(field).query(value).analyzer(analyzer));
  }

  /**
   * 多字段查询
   *
   * @param fields 多个字段
   * @param value 查询字符串
   * @return Query
   */
  public static Query multiMatch(String[] fields, String value) {
    Assert.isTrue(fields.length != 0, "param fields length must be greater than 1 ");
    return Query.of(QueryBuilders.multiMatch().fields(Arrays.asList(fields)).query(value));
  }

  /**
   * 多字段查询
   *
   * @param fields 多个字段
   * @param value 查询字符串
   * @param analyzer 分析器
   * @return Query
   */
  public static Query multiMatch(String[] fields, String value, String analyzer) {
    Assert.isTrue(fields.length != 0, "param fields length must be greater than 1 ");
    return Query.of(
        QueryBuilders.multiMatch().fields(Arrays.asList(fields)).query(value).analyzer(analyzer));
  }

  /**
   * 单个字段区间检索
   *
   * @param field 匹配字段
   * @param minValue 最小值
   * @param maxValue 最大值
   * @return Query
   */
  public static <T> Query range(String field, T minValue, T maxValue) {
    return Query.of(
        QueryBuilders.range().field(field).lte(JsonData.of(minValue)).gte(JsonData.of(maxValue)));
  }

  /**
   * 单个字段区间检索
   *
   * @param field 匹配字段
   * @param fn RangeQuery.Builder
   * @return Query
   */
  public static Query range(String field, Function<RangeQuery.Builder, RangeQuery.Builder> fn) {
    return Query.of(fn.apply(QueryBuilders.range().field(field)));
  }

  /**
   * 单个字段检索 - 时间区间
   *
   * @param field 匹配字段
   * @param begin 开始时间
   * @param end 结束时间
   * @return Query
   */
  public static Query range(String field, Date begin, Date end) {
    return range(field, begin, end, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * 单个字段检索 - 时间区间
   *
   * @param field 匹配字段
   * @param begin 开始时间
   * @param end 结束时间
   * @param format 时间格式
   * @return Query
   */
  public static Query range(String field, Date begin, Date end, String format) {
    return Query.of(
        QueryBuilders.range()
            .gte(JsonData.of(DateUtil.format(begin, format)))
            .lte(JsonData.of(DateUtil.format(end, format)))
            .format(format));
  }

  /**
   * 连接多个查询条件
   *
   * @param values 查询条件
   * @param clause 连接方式
   * @return Query
   */
  protected static Query combine(Query[] values, BooleanClause clause) {
    if (BooleanClause.must == clause) {
      return must(values);
    }
    if (BooleanClause.must_not == clause) {
      return mustNot(values);
    }
    if (BooleanClause.should == clause) {
      return should(values);
    }
    if (BooleanClause.filter == clause) {
      return filter(values);
    }
    throw new SearchException("不支持的连接方式:" + clause);
  }

  public static Query bool(Function<BoolQueryBuilder, BoolQueryBuilder> fn) {
    return Query.of(fn.apply(new BoolQueryBuilder(QueryBuilders.bool())).build());
  }

  /**
   * 必须匹配条件才能被包含进来。
   *
   * <p>相当于sql中的 and
   *
   * @param values 查询条件
   * @return Query
   */
  public static Query must(Query... values) {
    Assert.isTrue(values.length != 0, "param values length must be greater than 1 ");
    return Query.of(
        QueryBuilders.bool()
            .must(
                Arrays.stream(values)
                    .map(item -> item.toQuery()._toQuery())
                    .collect(Collectors.toList())));
  }

  /**
   * 不匹配这些条件才能被包含进来
   *
   * <p>相当于sql中的 not
   *
   * @param values 查询条件
   * @return Query
   */
  public static Query mustNot(Query... values) {
    Assert.isTrue(values.length != 0, "param values length must be greater than 1 ");
    return Query.of(
        QueryBuilders.bool()
            .mustNot(
                Arrays.stream(values)
                    .map(item -> item.toQuery()._toQuery())
                    .collect(Collectors.toList())));
  }

  /**
   * 多个字段模糊检索同一个值
   *
   * <p>相当于sql中的or 如果满足这些语句中的任意语句，将增加 _score, 否则，无任何影响。它们主要用于修正每个文档的相关性得分
   *
   * @param values 条件
   * @return Query
   */
  public static Query should(Query... values) {
    Assert.isTrue(values.length != 0, "param values length must be greater than 1 ");
    return new Query(
        QueryBuilders.bool()
            .should(
                Arrays.stream(values)
                    .map(item -> item.toQuery()._toQuery())
                    .collect(Collectors.toList())));
  }

  /**
   * 必须 匹配，但它以不评分、过滤模式来进行。
   *
   * <p>这些语句对评分没有贡献，只是根据过滤标准来排除或包含文档。
   *
   * @param values 条件
   * @return Query
   */
  public static Query filter(Query... values) {
    Assert.isTrue(values.length != 0, "param values length must be greater than 1 ");
    return new Query(
        QueryBuilders.bool()
            .filter(
                Arrays.stream(values)
                    .map(item -> item.toQuery()._toQuery())
                    .collect(Collectors.toList())));
  }

  public <T extends QueryVariant> T toQuery() {
    return (T) this.builder.build();
  }

  public final Query boost(@Nullable Float value) {
    if (this.builder instanceof AbstractBuilder) {
      ((AbstractBuilder) this.builder).boost(value);
    }
    return this;
  }

  public final Query queryName(@Nullable String value) {
    if (this.builder instanceof AbstractBuilder) {
      ((AbstractBuilder) this.builder).queryName(value);
    }
    return this;
  }

  static class BoolQueryBuilder {

    private final BoolQuery.Builder boolQueryBuilder;

    public BoolQueryBuilder(BoolQuery.Builder boolQueryBuilder) {
      this.boolQueryBuilder = boolQueryBuilder;
    }

    public BoolQueryBuilder must(Query... values) {
      boolQueryBuilder.must(((BoolQuery) Query.must(values).toQuery()).must());
      return this;
    }

    public BoolQueryBuilder mustNot(Query... values) {
      boolQueryBuilder.mustNot(((BoolQuery) Query.mustNot(values).toQuery()).must());
      return this;
    }

    public BoolQueryBuilder should(Query... values) {
      boolQueryBuilder.should(((BoolQuery) Query.should(values).toQuery()).must());
      return this;
    }

    public BoolQuery.Builder build() {
      return this.boolQueryBuilder;
    }
  }
}
