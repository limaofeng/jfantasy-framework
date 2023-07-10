package org.jfantasy.graphql.error;

import static graphql.ErrorType.ValidationError;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.kickstart.spring.error.ErrorContext;
import graphql.language.SourceLocation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfantasy.framework.error.ErrorResponse;
import org.jfantasy.framework.util.common.ObjectUtil;

/**
 * 默认的 GraphQLError
 *
 * @author limaofeng
 * @version V1.0
 * @date 2020/3/22 4:36 下午
 */
public class DefaultGraphQLError extends ErrorResponse implements GraphQLError {

  private List<Object> path;
  private final List<SourceLocation> locations;
  private final ErrorClassification errorType;

  public DefaultGraphQLError(ErrorContext errorContext) {
    this.path = errorContext.getPath();
    this.locations = errorContext.getLocations();
    this.errorType = ValidationError;
    this.setData(ObjectUtil.defaultValue(errorContext.getExtensions(), new HashMap<>()));
  }

  @Override
  public List<SourceLocation> getLocations() {
    return this.locations;
  }

  public void setPath(List<Object> path) {
    this.path = path;
  }

  @Override
  public List<Object> getPath() {
    return !this.path.isEmpty() ? this.path : null;
  }

  @Override
  public Map<String, Object> toSpecification() {
    Map<String, Object> result = GraphqlErrorHelper.toSpecification(this);
    Map<String, Object> extensions = (Map<String, Object>) result.get("extensions");
    extensions.put("code", this.getCode());
    extensions.put("timestamp", this.getTimestamp());
    if (!this.getFields().isEmpty()) {
      extensions.put("fields", this.getFields());
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
