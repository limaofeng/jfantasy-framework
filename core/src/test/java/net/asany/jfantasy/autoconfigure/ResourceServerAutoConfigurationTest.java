package net.asany.jfantasy.autoconfigure;

import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.auth.core.token.AuthorizationServerTokenServices;
import net.asany.jfantasy.framework.spring.ClassPathScanner;
import org.junit.jupiter.api.Test;

@Slf4j
class ResourceServerAutoConfigurationTest {

  @Test
  void tokenServiceFactory() {
    Set<Class<?>> classes =
        ClassPathScanner.getInstance()
            .findInterfaceClasses(
                "net.asany.jfantasy.framework.security", AuthorizationServerTokenServices.class);
    for (Class<?> clazz : classes) {
      log.debug(clazz.getName());
    }
  }
}
