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
package net.asany.jfantasy.graphql.gateway.error;

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
import net.asany.jfantasy.framework.error.ErrorUtils;
import net.asany.jfantasy.framework.error.FieldValidationError;
import net.asany.jfantasy.framework.util.common.DateUtil;

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
      if (this.extensions != null) {
        this.code = (String) dataFetchException.getExtensions().get("code");
        this.timestamp = (String) dataFetchException.getExtensions().get("timestamp");
      } else {
        this.code = "400000";
        this.timestamp = DateUtil.format("yyyy-MM-dd HH:mm:ss");
      }
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
    if (this.extensions != null && !this.extensions.isEmpty()) {
      Map<String, Object> result = new HashMap<>();
      result.put("message", this.message);
      result.put("path", this.path);
      result.put("locations", this.locations);
      result.put("extensions", this.extensions);
      return result;
    }

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
