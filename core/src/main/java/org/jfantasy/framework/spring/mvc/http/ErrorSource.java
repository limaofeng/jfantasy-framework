package org.jfantasy.framework.spring.mvc.http;

import org.jfantasy.framework.error.ErrorResponse;

public class ErrorSource {

  private ErrorResponse error;

  private Object state;

  public ErrorSource(ErrorResponse error, Object state) {
    this.error = error;
    this.state = state;
  }

  public ErrorResponse getError() {
    return error;
  }

  public <T> T getState() {
    return state == null ? null : (T) state;
  }
}
