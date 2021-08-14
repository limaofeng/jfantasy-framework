package org.jfantasy.graphql.error;

import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author limaofeng
 * @version V1.0
 * 
 * @date 2020/3/22 8:16 下午
 */
public class GraphQLStaticMethodMatcherPointcut extends StaticMethodMatcherPointcut {

    private ClassFilter classFilter;

    public GraphQLStaticMethodMatcherPointcut() {
        classFilter = new GraphQLClassFilter(new Class[]{GraphQLMutationResolver.class});
    }

    @Override
    public ClassFilter getClassFilter() {
        return this.classFilter;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Annotation[][] annotations = method.getParameterAnnotations();
        return Arrays.stream(annotations).anyMatch(item -> Arrays.stream(item).allMatch(annotation -> annotation.annotationType() == Validated.class));
    }
}
