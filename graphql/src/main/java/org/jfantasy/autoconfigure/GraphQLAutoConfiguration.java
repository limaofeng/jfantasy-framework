package org.jfantasy.autoconfigure;

import graphql.kickstart.tools.SchemaParserDictionary;
import graphql.kickstart.tools.boot.GraphQLJavaToolsAutoConfiguration;
import org.jfantasy.framework.security.authentication.dao.SimpleAuthenticationProvider;
import org.jfantasy.graphql.SchemaParserDictionaryBuilder;
import org.jfantasy.graphql.VersionGraphQLQueryResolver;
import org.jfantasy.graphql.client.GraphQLClientBeanPostProcessor;
import org.jfantasy.graphql.context.SharedAuthenticationToken;
import org.jfantasy.graphql.context.SharedUserDetailsService;
import org.jfantasy.graphql.errors.GraphQLResolverAdvice;
import org.jfantasy.graphql.errors.GraphQLStaticMethodMatcherPointcut;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019/8/23 6:18 下午
 */
@Configuration
@AutoConfigureBefore(GraphQLJavaToolsAutoConfiguration.class)
@ComponentScan({"org.jfantasy.graphql.context", "org.jfantasy.graphql.errors"})
public class GraphQLAutoConfiguration {

    @Bean
    static GraphQLClientBeanPostProcessor grpcClientBeanPostProcessor(final ApplicationContext applicationContext, final ResourceLoader resourceLoader) {
        return new GraphQLClientBeanPostProcessor(applicationContext, resourceLoader);
    }

    @Bean
    public OpenEntityManagerInViewFilter openEntityManagerInViewFilter() {
        return new OpenEntityManagerInViewFilter();
    }

    @Bean
    public SchemaParserDictionary schemaParserDictionary(List<SchemaParserDictionaryBuilder> builders) {
        SchemaParserDictionary dictionary = new SchemaParserDictionary();
        builders.stream().forEach(item -> item.build(dictionary));
        return dictionary;
    }

    @Bean
    public DefaultBeanFactoryPointcutAdvisor graphQLErrorPointcutAdvisor(@Autowired GraphQLResolverAdvice advice) {
        DefaultBeanFactoryPointcutAdvisor beanFactory = new DefaultBeanFactoryPointcutAdvisor();
        beanFactory.setPointcut(new GraphQLStaticMethodMatcherPointcut());
        beanFactory.setAdvice(advice);
        return beanFactory;
    }

    @Bean
    public VersionGraphQLQueryResolver versionGraphQLQueryResolver() {
        return new VersionGraphQLQueryResolver();
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return builder.build();
    }

    @Bean(name = "shared.AuthenticationProvider")
    @ConditionalOnBean(SharedUserDetailsService.class)
    public SimpleAuthenticationProvider simpleAuthenticationProvider(SharedUserDetailsService sharedUserDetailsService) {
        SimpleAuthenticationProvider provider = new SimpleAuthenticationProvider(SharedAuthenticationToken.class);
        provider.setUserDetailsService(sharedUserDetailsService);
        return provider;
    }

}
