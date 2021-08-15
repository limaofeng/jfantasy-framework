package org.jfantasy.framework.security.authentication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.security.AuthenticationException;
import org.jfantasy.framework.security.authentication.event.*;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

@Slf4j
public class DefaultAuthenticationEventPublisher
    implements AuthenticationEventPublisher, ApplicationEventPublisherAware {

  private ApplicationEventPublisher applicationEventPublisher;

  private final HashMap<String, Constructor<? extends AbstractAuthenticationEvent>>
      exceptionMappings = new HashMap<>();

  private Constructor<? extends AbstractAuthenticationFailureEvent>
      defaultAuthenticationFailureEventConstructor;

  public DefaultAuthenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
    addMapping(
        BadCredentialsException.class.getName(), AuthenticationFailureBadCredentialsEvent.class);
    addMapping(
        UsernameNotFoundException.class.getName(), AuthenticationFailureBadCredentialsEvent.class);
    addMapping(AccountExpiredException.class.getName(), AuthenticationFailureExpiredEvent.class);
    addMapping(
        ProviderNotFoundException.class.getName(),
        AuthenticationFailureProviderNotFoundEvent.class);
    addMapping(DisabledException.class.getName(), AuthenticationFailureDisabledEvent.class);
    addMapping(LockedException.class.getName(), AuthenticationFailureLockedEvent.class);
    addMapping(
        AuthenticationServiceException.class.getName(),
        AuthenticationFailureServiceExceptionEvent.class);
    addMapping(
        CredentialsExpiredException.class.getName(),
        AuthenticationFailureCredentialsExpiredEvent.class);
    addMapping(
        InvalidBearerTokenException.class.getName(),
        AuthenticationFailureBadCredentialsEvent.class);
  }

  @Override
  public void publishAuthenticationSuccess(Authentication authentication) {
    if (this.applicationEventPublisher != null) {
      this.applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(authentication));
    }
  }

  @Override
  public void publishAuthenticationFailure(
      AuthenticationException exception, Authentication authentication) {
    Constructor<? extends AbstractAuthenticationEvent> constructor = getEventConstructor(exception);
    AbstractAuthenticationEvent event = null;
    if (constructor != null) {
      try {
        event = constructor.newInstance(authentication, exception);
      } catch (IllegalAccessException
          | InvocationTargetException
          | InstantiationException ignored) {
      }
    }
    if (event != null) {
      if (this.applicationEventPublisher != null) {
        this.applicationEventPublisher.publishEvent(event);
      }
    } else {
      if (log.isDebugEnabled()) {
        log.debug("No event was found for the exception " + exception.getClass().getName());
      }
    }
  }

  private Constructor<? extends AbstractAuthenticationEvent> getEventConstructor(
      AuthenticationException exception) {
    Constructor<? extends AbstractAuthenticationEvent> eventConstructor =
        this.exceptionMappings.get(exception.getClass().getName());
    return (eventConstructor != null)
        ? eventConstructor
        : this.defaultAuthenticationFailureEventConstructor;
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Deprecated
  @SuppressWarnings({"unchecked"})
  public void setAdditionalExceptionMappings(Properties additionalExceptionMappings) {
    Assert.notNull(additionalExceptionMappings, "The exceptionMappings object must not be null");
    for (Object exceptionClass : additionalExceptionMappings.keySet()) {
      String eventClass = (String) additionalExceptionMappings.get(exceptionClass);
      try {
        Class<?> clazz = getClass().getClassLoader().loadClass(eventClass);
        Assert.isAssignable(AbstractAuthenticationFailureEvent.class, clazz);
        addMapping(
            (String) exceptionClass, (Class<? extends AbstractAuthenticationFailureEvent>) clazz);
      } catch (ClassNotFoundException ex) {
        throw new RuntimeException("Failed to load authentication event class " + eventClass);
      }
    }
  }

  public void setAdditionalExceptionMappings(
      Map<
              Class<? extends AuthenticationException>,
              Class<? extends AbstractAuthenticationFailureEvent>>
          mappings) {
    Assert.notEmpty(mappings, "The mappings Map must not be empty nor null");
    for (Map.Entry<
            Class<? extends AuthenticationException>,
            Class<? extends AbstractAuthenticationFailureEvent>>
        entry : mappings.entrySet()) {
      Class<?> exceptionClass = entry.getKey();
      Class<?> eventClass = entry.getValue();
      Assert.notNull(exceptionClass, "exceptionClass cannot be null");
      Assert.notNull(eventClass, "eventClass cannot be null");
      addMapping(
          exceptionClass.getName(),
          (Class<? extends AbstractAuthenticationFailureEvent>) eventClass);
    }
  }

  public void setDefaultAuthenticationFailureEvent(
      Class<? extends AbstractAuthenticationFailureEvent> defaultAuthenticationFailureEventClass) {
    Assert.notNull(
        defaultAuthenticationFailureEventClass,
        "defaultAuthenticationFailureEventClass must not be null");
    try {
      this.defaultAuthenticationFailureEventConstructor =
          defaultAuthenticationFailureEventClass.getConstructor(
              Authentication.class, AuthenticationException.class);
    } catch (NoSuchMethodException ex) {
      throw new RuntimeException(
          "Default Authentication Failure event class "
              + defaultAuthenticationFailureEventClass.getName()
              + " has no suitable constructor");
    }
  }

  private void addMapping(
      String exceptionClass, Class<? extends AbstractAuthenticationFailureEvent> eventClass) {
    try {
      Constructor<? extends AbstractAuthenticationEvent> constructor =
          eventClass.getConstructor(Authentication.class, AuthenticationException.class);
      this.exceptionMappings.put(exceptionClass, constructor);
    } catch (NoSuchMethodException ex) {
      throw new RuntimeException(
          "Authentication event class " + eventClass.getName() + " has no suitable constructor");
    }
  }
}
