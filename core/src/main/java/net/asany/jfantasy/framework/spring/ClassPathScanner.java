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
package net.asany.jfantasy.framework.spring;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.StopWatch;

@Slf4j
public class ClassPathScanner implements ResourceLoaderAware {
  protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

  @Getter private static final ClassPathScanner instance = new ClassPathScanner();

  private ResourcePatternResolver resourcePatternResolver =
      new PathMatchingResourcePatternResolver();

  private MetadataReaderFactory metadataReaderFactory =
      new CachingMetadataReaderFactory(this.resourcePatternResolver);

  @Setter @Getter private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

  @Override
  public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
    this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
    this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
  }

  private static final String CLASSPATH = "classpath*:";

  public Set<String> findTargetClassNames(String basePackage) {
    Set<String> candidates = new LinkedHashSet<>();
    try {
      String packageSearchPath =
          CLASSPATH
              + ClassUtil.convertClassNameToResourcePath(basePackage)
              + "/"
              + this.resourcePattern;
      Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
      for (Resource resource : resources) {
        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
        String clazzName = metadataReader.getClassMetadata().getClassName();
        candidates.add(clazzName);
        if (log.isDebugEnabled()) {
          log.debug("Find Class : {}", clazzName);
        }
      }
    } catch (IOException ex) {
      throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
    }
    return candidates;
  }

  /**
   * 查找Class 根据是否标注指定的注解
   *
   * @param <T> 注解泛型
   * @param basepackage 扫描路径
   * @param anno 注解
   * @return 标注注解的Class
   */
  public <T extends Annotation> Set<Class<?>> findAnnotationedClasses(
      String basepackage, Class<T> anno) {
    log.debug("Scanning %s in %s".formatted(anno, basepackage));
    StopWatch watch = new StopWatch();
    watch.start();
    Set<Class<?>> candidates = new LinkedHashSet<>();
    try {
      String packageSearchPath =
          "classpath*:"
              + ClassUtil.convertClassNameToResourcePath(basepackage)
              + "/"
              + this.resourcePattern;
      Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
      for (Resource resource : resources) {
        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
        if (!metadataReader.getAnnotationMetadata().hasAnnotation(anno.getName())) {
          continue;
        }
        try {
          String clazzName = metadataReader.getClassMetadata().getClassName();
          candidates.add(Class.forName(clazzName));
          log.debug("Find Annotationed Class {}(@{})", clazzName, anno.getName());
        } catch (ClassNotFoundException ignored) {
          log.error(ignored.getMessage());
        }
      }
    } catch (IOException ex) {
      throw new BeanDefinitionStoreException(
          "I/O failure during classpath scanning", ex); // NOSONAR
    }
    watch.stop();
    log.debug("Scaned in {} ms", watch.getTotalTimeMillis());
    return candidates;
  }

  /**
   * @param basepackage 扫描包
   * @param interfaceClass 接口或者父类
   * @return class
   */
  public <T> Set<Class<?>> findInterfaceClasses(String basepackage, Class<T> interfaceClass) {
    log.debug("Scanning {} in {}", interfaceClass, basepackage);
    StopWatch watch = new StopWatch();
    watch.start();
    Set<Class<?>> candidates = new LinkedHashSet<>();
    try {
      String packageSearchPath =
          "classpath*:"
              + ClassUtil.convertClassNameToResourcePath(basepackage)
              + "/"
              + this.resourcePattern;
      Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
      for (Resource resource : resources) {
        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        if (classMetadata.isInterface()) {
          continue;
        }
        String clazzName = metadataReader.getClassMetadata().getClassName();
        try {
          Class<?> clazz = Class.forName(clazzName);
          if (interfaceClass.isAssignableFrom(clazz)) {
            candidates.add(clazz);
          }
        } catch (ClassNotFoundException | NoClassDefFoundError localClassNotFoundException) {
          log.error(localClassNotFoundException.getMessage(), localClassNotFoundException);
        }
      }
    } catch (IOException ex) {
      throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
    }
    watch.stop();
    log.debug("Scaned in {} ms", watch.getTotalTimeMillis());
    return candidates;
  }
}
