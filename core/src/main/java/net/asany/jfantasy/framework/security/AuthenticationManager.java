package net.asany.jfantasy.framework.security;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.authentication.*;
import org.springframework.util.Assert;

/**
 * 身份验证管理器
 *
 * @author limaofeng
 */
@Slf4j
public class AuthenticationManager {

  private List<AuthenticationProvider<? extends Authentication>> providers = new ArrayList<>();

  private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();

  public AuthenticationManager() {}

  public AuthenticationManager(List<AuthenticationProvider<? extends Authentication>> providers) {
    this.providers = providers;
  }

  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Class<? extends Authentication> toTest = authentication.getClass();
    AuthenticationException lastException = null;
    Authentication result = null;
    int currentPosition = 0;
    int size = this.providers.size();

    for (AuthenticationProvider<? extends Authentication> provider : this.providers) {
      if (!provider.supports(toTest)) {
        continue;
      }
      if (log.isTraceEnabled()) {
        //noinspection PlaceholderCountMatchesArgumentCount
        log.trace(
            "Authenticating request with %s (%d/%d)",
            provider.getClass().getSimpleName(), ++currentPosition, size);
      }
      try {
        //noinspection unchecked
        result = ((AuthenticationProvider<Authentication>) provider).authenticate(authentication);
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
    if ((dest instanceof AbstractAuthenticationToken token) && (dest.getDetails() == null)) {
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

  public void addProvider(AuthenticationProvider<? extends Authentication> provider) {
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
