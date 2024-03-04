package org.jfantasy.graphql.error;

import graphql.kickstart.servlet.core.GraphQLServletListener;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.jfantasy.framework.error.ErrorResponse;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.framework.jackson.JSON;

public class TokenGraphQLServletListener implements GraphQLServletListener {
  @Override
  public RequestCallback onRequest(HttpServletRequest request, HttpServletResponse response) {
    return new RequestCallback() {

      @Override
      @SneakyThrows(Exception.class)
      public void onError(
          HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        ErrorResponse errorResponse = new ErrorResponse();
        if (Exception.class.isAssignableFrom(throwable.getClass())) {
          ErrorUtils.fill(errorResponse, (Exception) throwable);
        }
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.print(JSON.serialize(errorResponse));
        response.setStatus(401);
        response.flushBuffer();
      }
    };
  }
}
