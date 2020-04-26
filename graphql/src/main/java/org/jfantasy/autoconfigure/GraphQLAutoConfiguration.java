package org.jfantasy.autoconfigure;

import graphql.kickstart.execution.context.GraphQLContextBuilder;
import graphql.kickstart.tools.SchemaParserDictionary;
import org.jfantasy.graphql.context.SecurityGraphQLContextBuilder;
import org.jfantasy.graphql.errors.GraphQLResolverAdvice;
import org.jfantasy.graphql.errors.GraphQLStaticMethodMatcherPointcut;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019/8/23 6:18 下午
 */
@Configuration
@ComponentScan({"org.jfantasy.graphql.context", "org.jfantasy.graphql.errors"})
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
