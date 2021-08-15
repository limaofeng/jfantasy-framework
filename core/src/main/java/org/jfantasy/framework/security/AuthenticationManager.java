package org.jfantasy.framework.security;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.security.authentication.*;
import org.springframework.util.Assert;

/**
 * 身份验证管理器
 *
 * @author limaofeng
 */
@Slf4j
public class AuthenticationManager {

  private List<AuthenticationProvider> providers = new ArrayList<>();
  private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();

  public AuthenticationManager() {}

  public AuthenticationManager(List<AuthenticationProvider> providers) {
    this.providers = providers;
  }

  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Class<? extends Authentication> toTest = authentication.getClass();
    AuthenticationException lastException = null;
    AuthenticationException parentException = null;
    Authentication result = null;
    Authentication parentResult = null;
    int currentPosition = 0;
    int size = this.providers.size();

    SecurityContextHolder.clearContext();

    for (AuthenticationProvider provider : this.providers) {
      if (!provider.supports(toTest)) {
        continue;
      }
      if (log.isTraceEnabled()) {
        log.trace(
            "Authenticating request with %s (%d/%d)",
            provider.getClass().getSimpleName(), ++currentPosition, size);
      }
      try {
        result = provider.authenticate(authentication);
        if (result != null) {
          copyDetails(authentication, result);
          break;
        }
      } catch (AccountStatusException | InternalAuthenticationServiceException ex) {
        prepareException(ex, authentication);
        throw ex;
      } catch (AuthenticationException ex) {
        lastException = ex;
      }
    }

    if (result != null) {
      this.eventPublisher.publishAuthenticationSuccess(result);
      securityContext.setAuthentication(result);
      SecurityContextHolder.setContext(securityContext);
      return result;
    }

    if (lastException == null) {
      lastException =
          new ProviderNotFoundException(
              "ProviderManager.providerNotFound No AuthenticationProvider found for "
                  + toTest.getName());
    }
    prepareException(lastException, authentication);
    throw lastException;
  }

  private void copyDetails(Authentication source, Authentication dest) {
    if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
      AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;
      token.setDetails(source.getDetails());
    }
  }

  public void setAuthenticationEventPublisher(AuthenticationEventPublisher eventPublisher) {
    Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
    this.eventPublisher = eventPublisher;
  }

  private void prepareException(AuthenticationException ex, Authentication auth) {
    this.eventPublisher.publishAuthenticationFailure(ex, auth);
  }

  public void addProvider(AuthenticationProvider provider) {
    this.providers.add(provider);
  }

  private static final class NullEventPublisher implements AuthenticationEventPublisher {

    @Override
    public void publishAuthenticationFailure(
        AuthenticationException exception, Authentication authentication) {}

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {}
  }
}
