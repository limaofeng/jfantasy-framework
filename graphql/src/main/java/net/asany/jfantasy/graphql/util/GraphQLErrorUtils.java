/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
public class GraphQLErrorUtils {

  public static DefaultGraphQLError buildGraphqlError(
      ErrorContext context, String errorCode, Exception e) {
    DefaultGraphQLError error = new DefaultGraphQLError(e.getMessage(), context);
    ErrorUtils.populateErrorAttributesFromException(error, e);
    error.setCode(errorCode);
    return error;
  }

  public static DefaultGraphQLError buildGraphqlError(
      ErrorContext context, String errorCode, String message) {
    DefaultGraphQLError error = new DefaultGraphQLError(message, context);
    ErrorUtils.populateErrorAttributesFromException(error, new Exception(message));
    error.setCode(errorCode);
    return error;
  }

  public static DefaultGraphQLError buildGraphqlError(
      ErrorContext context, String errorCode, Exception e, Map<String, Object> extensions) {
    DefaultGraphQLError error = buildGraphqlError(context, errorCode, e);
    error.setData(extensions);
    return error;
  }

  public static DefaultGraphQLError buildGraphqlError(
      ErrorContext context, String errorCode, String message, Map<String, Object> extensions) {
    DefaultGraphQLError error = buildGraphqlError(context, errorCode, message);
    error.setData(extensions);
    return error;
  }

  public static DefaultGraphQLError buildGraphqlError(ErrorContext context, Exception e) {
    if (e instanceof UndeclaredThrowableException) {
      e = (Exception) ((UndeclaredThrowableException) e).getUndeclaredThrowable();
    }
    DefaultGraphQLError error;
    if (e instanceof MethodArgumentNotValidException) {
      error = new DefaultGraphQLError(e.getMessage(), context, ErrorType.ValidationError);
    } else if (e instanceof AuthenticationException) {
      error =
          new DefaultGraphQLError(
              e.getMessage(), context, AuthorizationErrorType.AuthenticatedError);
    } else {
      error = new DefaultGraphQLError(e.getMessage(), context);
    }
    ErrorUtils.populateErrorAttributesFromException(error, e);
    return error;
  }
}
