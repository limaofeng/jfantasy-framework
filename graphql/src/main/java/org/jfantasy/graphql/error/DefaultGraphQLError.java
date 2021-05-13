package org.jfantasy.graphql.error;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
import graphql.kickstart.spring.error.ErrorContext;
import graphql.language.SourceLocation;
import org.jfantasy.framework.error.ErrorResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static graphql.ErrorType.ValidationError;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/3/22 4:36 下午
 */
public class DefaultGraphQLError extends ErrorResponse implements GraphQLError {

    private List<Object> path = new ArrayList<>();
    private List<SourceLocation> locations;
    private ErrorClassification errorType = ValidationError;

    public DefaultGraphQLError() {
    }

    public DefaultGraphQLError(ErrorContext errorContext) {
        this.path = errorContext.getPath();
        this.errorType = errorContext.getErrorType();
        this.setData(errorContext.getExtensions());
        this.locations = errorContext.getLocations();
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
