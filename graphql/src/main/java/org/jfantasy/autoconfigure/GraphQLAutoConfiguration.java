package org.jfantasy.autoconfigure;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.coxautodev.graphql.tools.SchemaParserDictionary;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.graphql.errors.GraphQLResolverAdvice;
import org.jfantasy.graphql.errors.GraphQLStaticMethodMatcherPointcut;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019/8/23 6:18 下午
 */
@Configuration
@ComponentScan({"org.jfantasy.graphql.errors"})
public class GraphQLAutoConfiguration {

    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }

    @Bean
    public SchemaParserDictionary schemaParserDictionary() {
        return new SchemaParserDictionary();
    }

    @Bean
    public DefaultBeanFactoryPointcutAdvisor graphQLErrorPointcutAdvisor(@Autowired GraphQLResolverAdvice advice) {
        DefaultBeanFactoryPointcutAdvisor beanFactory = new DefaultBeanFactoryPointcutAdvisor();
        beanFactory.setPointcut(new GraphQLStaticMethodMatcherPointcut());
        beanFactory.setAdvice(advice);
        return beanFactory;
    }
}
