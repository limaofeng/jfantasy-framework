package org.jfantasy.graphql.error;

import graphql.GraphQLError;
import graphql.kickstart.spring.error.ErrorContext;
import org.jfantasy.graphql.util.GraphqlErrorUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2020/3/22 4:15 下午
 */
@Component
public class GraphqlErrorHandler {

  @ExceptionHandler(value = Exception.class)
  public GraphQLError transform(Exception e, ErrorContext errorContext) {
    if (e instanceof GraphQLError) {
      return (GraphQLError) e;
    }
    return GraphqlErrorUtil.buildGraphqlError(errorContext, e);
  }
}
