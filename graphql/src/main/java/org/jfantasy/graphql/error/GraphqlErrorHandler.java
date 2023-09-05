package org.jfantasy.graphql.error;

import graphql.GraphQLError;
import graphql.kickstart.spring.error.ErrorContext;
import org.jfantasy.graphql.util.GraphqlErrorUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Graphql 异常处理
 *
 * @author limaofeng
 * @version V1.0
 */
@Component
public class GraphqlErrorHandler {

  @ExceptionHandler(value = {Exception.class, RuntimeException.class})
  public GraphQLError transform(Exception e, ErrorContext errorContext) {
    if (e instanceof GraphQLError) {
      return (GraphQLError) e;
    }
    return GraphqlErrorUtil.buildGraphqlError(errorContext, e);
  }
}
