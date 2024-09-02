package net.asany.jfantasy.framework.security.core;

import java.util.HashMap;
import java.util.Map;

public class PrincipalAttributeServiceRegistry {

  private final Map<Class<? extends AuthenticatedPrincipal>, PrincipalAttributeService>
      serviceRegistry = new HashMap<>();

  public void registerService(
      Class<? extends AuthenticatedPrincipal> principalClass, PrincipalAttributeService service) {
    serviceRegistry.put(principalClass, service);
  }

  public PrincipalAttributeService getService(
      Class<? extends AuthenticatedPrincipal> principalClass) {
    return serviceRegistry.get(principalClass);
  }
}
