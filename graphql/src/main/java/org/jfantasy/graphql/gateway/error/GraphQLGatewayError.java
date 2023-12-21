package org.jfantasy.graphql.gateway.error;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.kickstart.spring.error.ErrorContext;
import graphql.language.SourceLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.framework.spring.mvc.http.FieldValidationError;
import org.jfantasy.framework.util.common.DateUtil;

public class GraphQLGatewayError implements GraphQLError {
  private final String message;
  private final String code;
  @Getter private final List<Object> path;
  private final List<SourceLocation> locations;
  private final ErrorClassification errorType;
  private final String timestamp;
  private final List<FieldValidationError> fieldErrors;
  private final Map<String, Object> extensions;
  private Map<String, Object> data = new HashMap<>();

  public GraphQLGatewayError(ErrorContext context, GraphQLGatewayException e) {
    this.message = e.getMessage();
    if (e instanceof GraphQLServiceDataFetchException dataFetchException) {
      this.path = dataFetchException.getPath();
      this.locations = dataFetchException.getLocations();
      this.extensions = dataFetchException.getExtensions();
      this.code = (String) dataFetchException.getExtensions().get("code");
      this.timestamp = (String) dataFetchException.getExtensions().get("timestamp");
      this.fieldErrors = dataFetchException.getFieldErrors();
      this.data = dataFetchException.getData();
      this.errorType = ErrorType.DataFetchingException;
    } else {
      Map<String, Object> errorAttributes = new HashMap<>();
      ErrorUtils.populateErrorAttributesFromException(errorAttributes, e);
      this.code = (String) errorAttributes.get("code");
      this.timestamp = DateUtil.format("yyyy-MM-dd HH:mm:ss");
      this.path = context.getPath();
      this.extensions = context.getExtensions();
      this.locations = context.getLocations();
      this.errorType = context.getErrorType();
      this.fieldErrors = new ArrayList<>();
    }
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public List<SourceLocation> getLocations() {
    return this.locations;
  }

  @Override
  public ErrorClassification getErrorType() {
    return this.errorType;
  }

  @Override
  public Map<String, Object> toSpecification() {
    Map<String, Object> result = GraphqlErrorHelper.toSpecification(this);
    @SuppressWarnings("unchecked")
    Map<String, Object> extensions = (Map<String, Object>) result.get("extensions");
    if (this.extensions == null || this.extensions.isEmpty()) {
      //noinspection unchecked
      extensions = (Map<String, Object>) result.get("extensions");
      extensions.put("code", this.code);
      extensions.put("timestamp", this.timestamp);
      if (this.fieldErrors != null && !this.fieldErrors.isEmpty()) {
        extensions.put("errors", this.fieldErrors);
      }
      if (this.data != null && !this.data.isEmpty()) {
        extensions.put("data", this.data);
      }
    } else {
      result.put("extensions", extensions);
    }
    return result;
  }
}
