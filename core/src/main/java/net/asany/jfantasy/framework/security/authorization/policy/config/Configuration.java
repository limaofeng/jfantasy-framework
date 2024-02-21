package net.asany.jfantasy.framework.security.authorization.policy.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.SneakyThrows;
import net.asany.jfantasy.framework.security.authorization.policy.PermissionPolicy;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

@Data
public class Configuration {

  private DefaultPolicy defaultPolicy;

  private List<PermissionPolicy> policies;

  private List<ConfigRole> roles;

  @SneakyThrows
  public static Configuration load(String path) {
    InputStream inputStream;
    if (path.startsWith("classpath:")) {
      path = path.substring("classpath:".length());
      inputStream = ClassLoader.getSystemResourceAsStream(path);
    } else {
      inputStream = Files.newInputStream(new File(path).toPath());
    }
    Constructor constructor = new PolicyConstructor();
    PropertyUtils propertyUtils = new PolicyPropertyUtils();
    propertyUtils.setSkipMissingProperties(true);
    constructor.setPropertyUtils(propertyUtils);
    Yaml yaml = new Yaml(constructor);
    //noinspection VulnerableCodeUsages
    return yaml.load(inputStream);
  }

  public List<PermissionPolicy> getPoliciesForUser(String username) {
    return this.policies;
  }

  public List<PermissionPolicy> getPolicyForRole(String role) {
    return this.policies;
  }

  public Optional<PermissionPolicy> getPolicyById(String id) {
    return this.policies.stream().filter(p -> p.getId().equals(id)).findFirst();
  }
}
