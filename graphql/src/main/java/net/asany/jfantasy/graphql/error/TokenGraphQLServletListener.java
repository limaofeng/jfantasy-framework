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

import graphql.kickstart.servlet.core.GraphQLServletListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenGraphQLServletListener implements GraphQLServletListener {
  @Override
  public RequestCallback onRequest(HttpServletRequest request, HttpServletResponse response) {
    return new RequestCallback() {

      @Override
      @SneakyThrows(Exception.class)
      public void onError(
          HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        //        ErrorResponse errorResponse = new ErrorResponse();
        //        if (Exception.class.isAssignableFrom(throwable.getClass())) {
        //          ErrorUtils.populateErrorAttributesFromException(errorResponse, throwable);
        //        }
        //        response.setHeader("Content-Type", "application/json;charset=utf-8");
        //        PrintWriter printWriter = response.getWriter();
        //        printWriter.print(JSON.serialize(errorResponse));
        //        response.setStatus(401);
        //        response.flushBuffer();
        log.debug("onError: {}", throwable.getMessage());
      }
    };
  }
}
