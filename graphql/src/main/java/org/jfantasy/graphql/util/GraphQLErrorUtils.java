package org.jfantasy.graphql.util;

import graphql.kickstart.spring.error.ErrorContext;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.graphql.error.DefaultGraphQLError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;

/**
 * GraphQL 异常工具类
 *
 * @author limaofeng
 */
public class GraphQLErrorUtils {

    public static DefaultGraphQLError buildGraphQLError(ErrorContext context, String errorCode, Exception e) {
        DefaultGraphQLError error = new DefaultGraphQLError(context);
        ErrorUtils.fill(error, e);
        error.setCode(errorCode);
        return error;
    }

    public static DefaultGraphQLError buildGraphQLError(ErrorContext context, String errorCode, String message) {
        DefaultGraphQLError error = new DefaultGraphQLError(context);
        ErrorUtils.fill(error, new Exception(message));
        error.setCode(errorCode);
        return error;
    }

    public static DefaultGraphQLError buildGraphQLError(ErrorContext context, String errorCode, Exception e, Map<String, Object> extensions) {
        DefaultGraphQLError error = new DefaultGraphQLError(context);
        ErrorUtils.fill(error, e);
        error.setCode(errorCode);
        error.setData(extensions);
        return error;
    }

    public static DefaultGraphQLError buildGraphQLError(ErrorContext context, String errorCode, String message, Map<String, Object> extensions) {
        DefaultGraphQLError error = new DefaultGraphQLError(context);
        ErrorUtils.fill(error, new Exception(message));
        error.setCode(errorCode);
        error.setData(extensions);
        return error;
    }

    public static DefaultGraphQLError buildGraphQLError(ErrorContext context, Exception e) {
        DefaultGraphQLError error = new DefaultGraphQLError(context);
        if (e instanceof UndeclaredThrowableException) {
            e = (Exception) ((UndeclaredThrowableException) e).getUndeclaredThrowable();
        }
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = ((MethodArgumentNotValidException) e);
            ErrorUtils.fill(error, ex);
        } else {
            ErrorUtils.fill(error, e);
        }
        return error;
    }

}
