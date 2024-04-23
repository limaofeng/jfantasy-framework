package net.asany.jfantasy.graphql.error;

import graphql.GraphQLError;
import graphql.kickstart.spring.error.ErrorContext;
import net.asany.jfantasy.graphql.util.GraphQLErrorUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Graphql 异常处理
 *
 * @author limaofeng
 * @version V1.0
 */
@Component
public class GraphQLErrorHandler {

  @ExceptionHandler(value = {Exception.class, RuntimeException.class})
  public GraphQLError transform(Exception e, ErrorContext errorContext) {
    if (e instanceof GraphQLError) {
      return (GraphQLError) e;
    }
    return GraphQLErrorUtils.buildGraphqlError(errorContext, e);
  }
}
