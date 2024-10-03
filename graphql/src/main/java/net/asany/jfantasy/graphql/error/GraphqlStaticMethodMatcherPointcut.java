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
package net.asany.jfantasy.graphql.error;

import graphql.kickstart.tools.GraphQLMutationResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.validation.annotation.Validated;

/**
 * 静态方法匹配器切入点
 *
 * @author limaofeng
 * @version V1.0
 */
@Slf4j
public class GraphqlStaticMethodMatcherPointcut extends StaticMethodMatcherPointcut {

  private final ClassFilter classFilter;

  public GraphqlStaticMethodMatcherPointcut() {
    classFilter = new GraphQLClassFilter(new Class[] {GraphQLMutationResolver.class});
  }

  @NotNull
  @Override
  public ClassFilter getClassFilter() {
    return this.classFilter;
  }

  @Override
  public boolean matches(Method method, @NotNull Class<?> targetClass) {
    Annotation[][] annotations = method.getParameterAnnotations();
    return Arrays.stream(annotations)
        .anyMatch(
            item ->
                Arrays.stream(item)
                    .allMatch(annotation -> annotation.annotationType() == Validated.class));
  }
}
