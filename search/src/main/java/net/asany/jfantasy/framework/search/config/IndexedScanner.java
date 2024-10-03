/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.search.config;

import java.lang.annotation.Annotation;
import java.util.*;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public class IndexedScanner {

  private final ApplicationContext context;

  /**
   * Create a new {@link IndexedScanner} instance.
   *
   * @param context the source application context
   */
  public IndexedScanner(ApplicationContext context) {
    Assert.notNull(context, "Context must not be null");
    this.context = context;
  }

  /**
   * Scan for entities with the specified annotations.
   *
   * @param annotationTypes the annotation types used on the entities
   * @return a set of entity classes
   * @throws ClassNotFoundException if an entity class cannot be loaded
   */
  @SafeVarargs
  public final Set<Class<?>> scan(Class<? extends Annotation>... annotationTypes)
      throws ClassNotFoundException {
    List<String> packages = getPackages();
    if (packages.isEmpty()) {
      return Collections.emptySet();
    }
    ClassPathScanningCandidateComponentProvider scanner =
        createClassPathScanningCandidateComponentProvider(this.context);
    for (Class<? extends Annotation> annotationType : annotationTypes) {
      scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));
    }
    Set<Class<?>> entitySet = new HashSet<>();
    for (String basePackage : packages) {
      if (StringUtils.hasText(basePackage)) {
        for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
          entitySet.add(
              ClassUtils.forName(
                  Objects.requireNonNull(candidate.getBeanClassName()),
                  this.context.getClassLoader()));
        }
      }
    }
    return entitySet;
  }

  /**
   * Create a {@link ClassPathScanningCandidateComponentProvider} to scan entities based on the
   * specified {@link ApplicationContext}.
   *
   * @param context the {@link ApplicationContext} to use
   * @return a {@link ClassPathScanningCandidateComponentProvider} suitable to scan entities
   * @since 2.4.0
   */
  protected ClassPathScanningCandidateComponentProvider
      createClassPathScanningCandidateComponentProvider(ApplicationContext context) {
    ClassPathScanningCandidateComponentProvider scanner =
        new ClassPathScanningCandidateComponentProvider(false);
    scanner.setEnvironment(context.getEnvironment());
    scanner.setResourceLoader(context);
    return scanner;
  }

  private List<String> getPackages() {
    List<String> packages = IndexedScanPackages.get(this.context).getPackageNames();
    if (packages.isEmpty() && AutoConfigurationPackages.has(this.context)) {
      packages = AutoConfigurationPackages.get(this.context);
    }
    return packages;
  }
}
