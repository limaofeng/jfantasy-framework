package net.asany.jfantasy.graphql.gateway.error;

import graphql.GraphQLError;
import graphql.kickstart.spring.error.ErrorContext;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GraphQLGatewayErrorHandler {

  @ExceptionHandler(value = {GraphQLGatewayException.class})
  public GraphQLError gatewayErrorTransform(GraphQLGatewayException e, ErrorContext errorContext) {
    if (e instanceof GraphQLError) {
      return (GraphQLError) e;
    }
    return buildGraphqlError(errorContext, e);
  }

  public GraphQLError buildGraphqlError(ErrorContext context, GraphQLGatewayException e) {
    return new GraphQLGatewayError(context, e);
  }
}
