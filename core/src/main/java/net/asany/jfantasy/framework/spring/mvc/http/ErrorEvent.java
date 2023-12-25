package net.asany.jfantasy.framework.spring.mvc.http;

import net.asany.jfantasy.framework.error.ErrorResponse;
import org.springframework.context.ApplicationEvent;

public class ErrorEvent extends ApplicationEvent {

  public ErrorEvent(WebError error, Object state) {
    super(new ErrorSource(error, state));
  }

  public ErrorResponse getError() {
    return ((ErrorSource) this.getSource()).getError();
  }

  public <T> T getState() {
    return ((ErrorSource) this.getSource()).getState();
  }
}
