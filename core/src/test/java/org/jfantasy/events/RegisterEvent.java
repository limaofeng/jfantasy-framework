package org.jfantasy.events;

import org.springframework.context.ApplicationEvent;

public class RegisterEvent extends ApplicationEvent {

  public RegisterEvent(Object user) {
    super(user);
  }
}
