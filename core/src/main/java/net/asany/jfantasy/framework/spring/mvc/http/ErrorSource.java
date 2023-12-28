package net.asany.jfantasy.framework.spring.mvc.http;

import lombok.Getter;
import net.asany.jfantasy.framework.error.ErrorResponse;

public class ErrorSource {

  @Getter private final ErrorResponse error;

  private final Object state;

  public ErrorSource(ErrorResponse error, Object state) {
    this.error = error;
    this.state = state;
  }

  public <T> T getState() {
    return state == null ? null : (T) state;
  }
}
