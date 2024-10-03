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
package net.asany.jfantasy.graphql.error;

import graphql.GraphQLError;
import graphql.kickstart.spring.error.ErrorContext;
import net.asany.jfantasy.graphql.util.GraphQLErrorUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Graphql 异常处理
 *
 * @author limaofeng
 * @version V1.0
 */
@Component
public class GraphQLErrorHandler {

  @ExceptionHandler(value = {Exception.class, RuntimeException.class})
  public GraphQLError transform(Exception e, ErrorContext errorContext) {
    if (e instanceof GraphQLError) {
      return (GraphQLError) e;
    }
    return GraphQLErrorUtils.buildGraphqlError(errorContext, e);
  }
}
