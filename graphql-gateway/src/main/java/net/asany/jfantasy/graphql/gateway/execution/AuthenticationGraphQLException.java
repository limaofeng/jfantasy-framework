package net.asany.jfantasy.graphql.gateway.execution;

import graphql.execution.ResultPath;
import lombok.Getter;
import net.asany.jfantasy.graphql.gateway.error.GraphQLGatewayException;

@Getter
public class AuthenticationGraphQLException extends GraphQLGatewayException {

  private final ResultPath path;

  public AuthenticationGraphQLException(ResultPath path, String message) {
    super(message);
    this.path = path;
  }
}
