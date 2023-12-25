package net.asany.jfantasy.framework.security.authentication.event;

import net.asany.jfantasy.framework.security.authentication.Authentication;
import org.springframework.context.ApplicationEvent;

public abstract class AbstractAuthenticationEvent extends ApplicationEvent {

  public AbstractAuthenticationEvent(Authentication authentication) {
    super(authentication);
  }

  public Authentication getAuthentication() {
    return (Authentication) super.getSource();
  }
}
