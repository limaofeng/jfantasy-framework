package org.jfantasy.graphql.scalars;

import graphql.kickstart.autoconfigure.tools.GraphQLJavaToolsAutoConfiguration;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;

/**
 * GraphQL ScalarType Configuration
 *
 * @author limaofeng
 * @version V1.0
 */
@Slf4j
@AutoConfigureBefore(GraphQLJavaToolsAutoConfiguration.class)
public class GraphQLScalarTypeConfiguration {

  @Bean
  public GraphQLScalarType jsonScalar() {
    return ExtendedScalars.Json;
  }

  @Bean
  public GraphQLScalarType dateTimeScalar() {
    return ExtendedScalars.DateTime;
  }

  @Bean
  public GraphQLScalarType graphQLBigDecimalScalar() {
    return ExtendedScalars.GraphQLBigDecimal;
  }

  @Bean
  public GraphQLScalarType orderByScalar() {
    return GraphQLScalarType.newScalar()
        .name("OrderBy")
        .description("排序对象, 格式如：createdAt_ASC ")
        .coercing(new OrderCoercing())
        .build();
  }

  @Bean
  public GraphQLScalarType dateScalar() {
    return GraphQLScalarType.newScalar()
        .name("Date")
        .description(" Date 转换类")
        .coercing(new GraphqlDateCoercing())
        .build();
  }

  @Bean
  public GraphQLScalarType numberScalar() {
    return GraphQLScalarType.newScalar()
        .name("Number")
        .description(" Number 转换类")
        .coercing(new GraphqlNumberCoercing())
        .build();
  }

  @Bean
  public GraphQLScalarType dateBetweenScalar() {
    return GraphQLScalarType.newScalar()
        .name("DateBetween")
        .description("时间区间参数")
        .coercing(new GraphqlBetweenCoercing())
        .build();
  }
}
