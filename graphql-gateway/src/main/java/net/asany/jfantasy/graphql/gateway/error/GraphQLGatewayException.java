package net.asany.jfantasy.graphql.gateway.error;

import graphql.GraphQLException;

public class GraphQLGatewayException extends GraphQLException {
  public GraphQLGatewayException(String message) {
    super(message);
  }

  public GraphQLGatewayException(String message, Throwable cause) {
    super(message, cause);
  }
}
