package net.asany.jfantasy.graphql.util;

import graphql.ErrorType;
import graphql.kickstart.spring.error.ErrorContext;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Map;
import net.asany.jfantasy.framework.error.ErrorUtils;
import net.asany.jfantasy.framework.security.AuthenticationException;
import net.asany.jfantasy.graphql.error.AuthorizationErrorType;
import net.asany.jfantasy.graphql.error.DefaultGraphQLError;
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
}
