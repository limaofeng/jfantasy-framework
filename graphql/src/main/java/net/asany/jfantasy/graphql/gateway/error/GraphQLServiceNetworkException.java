package net.asany.jfantasy.graphql.gateway.error;

public class GraphQLServiceNetworkException extends GraphQLGatewayException {

  public GraphQLServiceNetworkException(String message) {
    super(message);
  }

  public GraphQLServiceNetworkException(String message, Throwable cause) {
    super(message, cause);
  }
}
