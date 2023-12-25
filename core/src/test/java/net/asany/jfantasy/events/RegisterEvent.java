package net.asany.jfantasy.events;

import org.springframework.context.ApplicationEvent;

public class RegisterEvent extends ApplicationEvent {

  public RegisterEvent(Object user) {
    super(user);
  }
}
