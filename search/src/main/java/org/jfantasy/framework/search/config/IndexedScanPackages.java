package org.jfantasy.framework.search.config;

import java.util.*;
import java.util.function.Supplier;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@Getter
public class IndexedScanPackages {

  private static final String BEAN = IndexedScanPackages.class.getName();

  private static final IndexedScanPackages NONE = new IndexedScanPackages();

  /**
   * -- GETTER -- Return the package names specified from all annotations.
   *
   * @return the entity scan package names
   */
  private final List<String> packageNames;

  IndexedScanPackages(String... packageNames) {
    List<String> packages = new ArrayList<>();
    for (String name : packageNames) {
      if (StringUtils.hasText(name)) {
        packages.add(name);
      }
    }
    this.packageNames = Collections.unmodifiableList(packages);
  }

  /**
   * Return the {@link IndexedScanPackages} for the given bean factory.
   *
   * @param beanFactory the source bean factory
   * @return the {@link IndexedScanPackages} for the bean factory (never {@code null})
   */
  public static IndexedScanPackages get(BeanFactory beanFactory) {
    // Currently we only store a single base package, but we return a list to
    // allow this to change in the future if needed
    try {
      return beanFactory.getBean(BEAN, IndexedScanPackages.class);
    } catch (NoSuchBeanDefinitionException ex) {
      return NONE;
    }
  }

  /**
   * Register the specified entity scan packages with the system.
   *
   * @param registry the source registry
   * @param packageNames the package names to register
   */
  public static void register(BeanDefinitionRegistry registry, String... packageNames) {
    Assert.notNull(registry, "Registry must not be null");
    Assert.notNull(packageNames, "PackageNames must not be null");
    register(registry, Arrays.asList(packageNames));
  }

  /**
   * Register the specified entity scan packages with the system.
   *
   * @param registry the source registry
   * @param packageNames the package names to register
   */
  public static void register(BeanDefinitionRegistry registry, Collection<String> packageNames) {
    Assert.notNull(registry, "Registry must not be null");
    Assert.notNull(packageNames, "PackageNames must not be null");
    if (registry.containsBeanDefinition(BEAN)) {
      IndexedScanPackages.IndexedScanPackagesBeanDefinition beanDefinition =
          (IndexedScanPackages.IndexedScanPackagesBeanDefinition) registry.getBeanDefinition(BEAN);
      beanDefinition.addPackageNames(packageNames);
    } else {
      registry.registerBeanDefinition(
          BEAN, new IndexedScanPackages.IndexedScanPackagesBeanDefinition(packageNames));
    }
  }

  /**
   * {@link ImportBeanDefinitionRegistrar} to store the base package from the importing
   * configuration.
   */
  static class Registrar implements ImportBeanDefinitionRegistrar {

    private final Environment environment;

    Registrar(Environment environment) {
      this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(
        @NotNull AnnotationMetadata metadata, @NotNull BeanDefinitionRegistry registry) {
      register(registry, getPackagesToScan(metadata));
    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata) {
      AnnotationAttributes attributes =
          AnnotationAttributes.fromMap(
              metadata.getAnnotationAttributes(IndexedScan.class.getName()));
      Set<String> packagesToScan = new LinkedHashSet<>();
      assert attributes != null;
      for (String basePackage : attributes.getStringArray("basePackages")) {
        addResolvedPackage(basePackage, packagesToScan);
      }
      for (Class<?> basePackageClass : attributes.getClassArray("basePackageClasses")) {
        addResolvedPackage(ClassUtils.getPackageName(basePackageClass), packagesToScan);
      }
      if (packagesToScan.isEmpty()) {
        String packageName = ClassUtils.getPackageName(metadata.getClassName());
        Assert.state(
            StringUtils.hasLength(packageName),
            "@IndexedScan cannot be used with the default package");
        return Collections.singleton(packageName);
      }
      return packagesToScan;
    }

    private void addResolvedPackage(String packageName, Set<String> packagesToScan) {
      packagesToScan.add(this.environment.resolvePlaceholders(packageName));
    }
  }

  static class IndexedScanPackagesBeanDefinition extends GenericBeanDefinition {

    private final Set<String> packageNames = new LinkedHashSet<>();

    IndexedScanPackagesBeanDefinition(Collection<String> packageNames) {
      setBeanClass(IndexedScanPackages.class);
      setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
      addPackageNames(packageNames);
    }

    @Override
    public Supplier<?> getInstanceSupplier() {
      return () -> new IndexedScanPackages(StringUtils.toStringArray(this.packageNames));
    }

    private void addPackageNames(Collection<String> additionalPackageNames) {
      this.packageNames.addAll(additionalPackageNames);
    }
  }
}
