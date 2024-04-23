package net.asany.jfantasy.framework.security.auth.core;

import java.util.HashMap;
import java.util.Map;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class TokenServiceFactory implements ApplicationContextAware {

  private ApplicationContext applicationContext;

  private final Map<
          Class<? extends AuthToken>,
          Class<? extends AuthorizationServerTokenServices<? extends AuthToken>>>
      tokenServicesMap = new HashMap<>();

  public <T> T getTokenServices(Class<? extends AuthToken> type) {
    Class<?> serviceClass = tokenServicesMap.get(type);
    if (serviceClass != null) {
      //noinspection unchecked
      return (T) applicationContext.getBean(serviceClass);
    }
    throw new IllegalArgumentException("No token service registered for type: " + type);
  }

  public void registerTokenService(
      Class<? extends AuthToken> tokenType,
      Class<? extends AuthorizationServerTokenServices<? extends AuthToken>> serviceClass) {
    tokenServicesMap.put(tokenType, serviceClass);
  }

  @Override
  public void setApplicationContext(@NotNull ApplicationContext applicationContext)
      throws BeansException {
    this.applicationContext = applicationContext;
  }
}
