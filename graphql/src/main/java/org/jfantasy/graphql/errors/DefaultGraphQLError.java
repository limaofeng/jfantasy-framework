package org.jfantasy.graphql.errors;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorHelper;
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

    public DefaultGraphQLError() {
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    public void addPath(String path) {
        this.path.add(path);
    }

    @Override
    public List<Object> getPath() {
        return !this.path.isEmpty() ? this.path : null;
    }

    @Override
    public Map<String, Object> toSpecification() {
        Map<String, Object> result = GraphqlErrorHelper.toSpecification(this);
        result.put("code", this.getCode());
        result.put("timestamp", this.getTimestamp());
        if (!this.getFields().isEmpty()) {
            result.put("fields", this.getFields());
        }
        return result;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ValidationError;
    }

}
