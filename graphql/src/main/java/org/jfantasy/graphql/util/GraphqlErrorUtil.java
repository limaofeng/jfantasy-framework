package org.jfantasy.graphql.util;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.kickstart.spring.error.ErrorContext;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.graphql.error.AuthorizationErrorType;
import org.jfantasy.graphql.error.DefaultGraphQLError;
import org.jfantasy.graphql.gateway.error.GraphQLGatewayError;
import org.jfantasy.graphql.gateway.error.GraphQLGatewayException;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * GraphQL 异常工具类
 *
 * @author limaofeng
 */
public class GraphqlErrorUtil {

  public static DefaultGraphQLError buildGraphqlError(
      ErrorContext context, String errorCode, Exception e) {
    DefaultGraphQLError error = new DefaultGraphQLError(context);
    ErrorUtils.populateErrorAttributesFromException(error, e);
    error.setCode(errorCode);
    return error;
  }

  public static DefaultGraphQLError buildGraphqlError(
      ErrorContext context, String errorCode, String message) {
    DefaultGraphQLError error = new DefaultGraphQLError(context);
    ErrorUtils.populateErrorAttributesFromException(error, new Exception(message));
    error.setCode(errorCode);
    return error;
  }

  public static DefaultGraphQLError buildGraphqlError(
      ErrorContext context, String errorCode, Exception e, Map<String, Object> extensions) {
    DefaultGraphQLError error = new DefaultGraphQLError(context);
    ErrorUtils.populateErrorAttributesFromException(error, e);
    error.setCode(errorCode);
    error.setData(extensions);
    return error;
  }

  public static DefaultGraphQLError buildGraphqlError(
      ErrorContext context, String errorCode, String message, Map<String, Object> extensions) {
    DefaultGraphQLError error = new DefaultGraphQLError(context);
    ErrorUtils.populateErrorAttributesFromException(error, new Exception(message));
    error.setCode(errorCode);
    error.setData(extensions);
    return error;
  }

  public static DefaultGraphQLError buildGraphqlError(ErrorContext context, Exception e) {
    if (e instanceof UndeclaredThrowableException) {
      e = (Exception) ((UndeclaredThrowableException) e).getUndeclaredThrowable();
    }
    DefaultGraphQLError error;
    if (e instanceof MethodArgumentNotValidException) {
      error = new DefaultGraphQLError(context, ErrorType.ValidationError);
    } else if (e instanceof AuthenticationException) {
      error = new DefaultGraphQLError(context, AuthorizationErrorType.AuthenticatedError);
    } else {
      error = new DefaultGraphQLError(context);
    }
    ErrorUtils.populateErrorAttributesFromException(error, e);
    return error;
  }

  public static GraphQLError buildGraphqlError(ErrorContext context, GraphQLGatewayException e) {
    return new GraphQLGatewayError(context, e);
  }
}
