package net.asany.jfantasy.graphql.error;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.kickstart.spring.error.ErrorContext;
import graphql.language.SourceLocation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.asany.jfantasy.framework.error.ErrorResponse;
import net.asany.jfantasy.framework.util.common.ObjectUtil;

/**
 * 默认的 GraphQLError
 *
 * @author limaofeng
 * @version V1.0
 */
public class DefaultGraphQLError extends ErrorResponse implements GraphQLError {

  private final List<Object> path;
  private final List<SourceLocation> locations;
  private final ErrorClassification errorType;

  public DefaultGraphQLError(String message, ErrorContext errorContext) {
    this(message, errorContext, errorContext.getErrorType());
  }

  public DefaultGraphQLError(
      String message, ErrorContext errorContext, ErrorClassification errorType) {
    this.path = errorContext.getPath();
    this.locations = errorContext.getLocations();
    this.errorType = errorType;
    this.setMessage(message);
    this.setData(ObjectUtil.defaultValue(errorContext.getExtensions(), new HashMap<>()));
  }

  @Override
  public List<SourceLocation> getLocations() {
    return this.locations;
  }

  @Override
  public List<Object> getPath() {
    return !this.path.isEmpty() ? this.path : null;
  }

  @Override
  public Map<String, Object> toSpecification() {
    Map<String, Object> result = GraphqlErrorHelper.toSpecification(this);
    @SuppressWarnings("unchecked")
    Map<String, Object> extensions = (Map<String, Object>) result.get("extensions");
    extensions.put("code", this.getCode());
    extensions.put("timestamp", this.getTimestamp());
    if (!this.getFieldErrors().isEmpty()) {
      extensions.put("errors", this.getFieldErrors());
    }
    if (!this.getData().isEmpty()) {
      extensions.put("data", this.getData());
    }
    return result;
  }

  @Override
  public ErrorClassification getErrorType() {
    return errorType;
  }
}
