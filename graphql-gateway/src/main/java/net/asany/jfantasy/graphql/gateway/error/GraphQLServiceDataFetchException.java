package net.asany.jfantasy.graphql.gateway.error;

import graphql.language.SourceLocation;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import net.asany.jfantasy.framework.error.FieldValidationError;

@Getter
public class GraphQLServiceDataFetchException extends GraphQLGatewayException {

  private final List<SourceLocation> locations;
  private final List<Object> path;
  private final Map<String, Object> extensions;
  private final List<FieldValidationError> fieldErrors;
  private final Map<String, Object> data;

  public GraphQLServiceDataFetchException(DataFetchGraphQLError error) {
    super(error.getMessage());
    this.locations = error.getLocations();
    this.path = error.getPath();
    this.extensions = error.getExtensions();
    this.data = error.getData();
    this.fieldErrors = error.getFieldErrors();
  }
}
