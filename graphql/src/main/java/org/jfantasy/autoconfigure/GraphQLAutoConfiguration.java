package org.jfantasy.autoconfigure;

import graphql.execution.ExecutionStrategy;
import graphql.execution.instrumentation.Instrumentation;
import graphql.kickstart.autoconfigure.tools.GraphQLJavaToolsAutoConfiguration;
import graphql.kickstart.autoconfigure.web.servlet.GraphQLWebAutoConfiguration;
import graphql.kickstart.tools.SchemaParserDictionary;
import java.util.List;
import org.jfantasy.graphql.SchemaParserDictionaryBuilder;
import org.jfantasy.graphql.client.GraphQLClientBeanPostProcessor;
import org.jfantasy.graphql.error.GraphQLResolverAdvice;
import org.jfantasy.graphql.error.GraphQLStaticMethodMatcherPointcut;
import org.jfantasy.graphql.execution.AsyncMutationExecutionStrategy;
import org.jfantasy.graphql.execution.AsyncQueryExecutionStrategy;
import org.jfantasy.graphql.execution.GraphQLTransactionInstrumentation;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

/**
 * GraphQL 自动配置
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019/8/23 6:18 下午
 */
@Configuration
@AutoConfigureBefore(GraphQLJavaToolsAutoConfiguration.class)
@ComponentScan({"org.jfantasy.graphql.context", "org.jfantasy.graphql.error"})
public class GraphQLAutoConfiguration {

  @Bean
  static GraphQLClientBeanPostProcessor clientBeanPostProcessor(
      final ApplicationContext applicationContext, final ResourceLoader resourceLoader) {
    return new GraphQLClientBeanPostProcessor(applicationContext, resourceLoader);
  }

  @Bean(GraphQLWebAutoConfiguration.QUERY_EXECUTION_STRATEGY)
  public ExecutionStrategy queryExecutionStrategy() {
    return new AsyncQueryExecutionStrategy();
  }

  @Bean(GraphQLWebAutoConfiguration.MUTATION_EXECUTION_STRATEGY)
  public ExecutionStrategy mutationExecutionStrategy() {
    return new AsyncMutationExecutionStrategy();
  }

  @Bean
  public Instrumentation getInstrumentation(PlatformTransactionManager transactionManager) {
    return new GraphQLTransactionInstrumentation(transactionManager);
  }

  @Bean
  public SchemaParserDictionary schemaParserDictionary(
      List<SchemaParserDictionaryBuilder> builders) {
    SchemaParserDictionary dictionary = new SchemaParserDictionary();
    builders.forEach(item -> item.build(dictionary));
    return dictionary;
  }

  @Bean
  public DefaultBeanFactoryPointcutAdvisor graphQLErrorPointcutAdvisor(
      @Autowired GraphQLResolverAdvice advice) {
    DefaultBeanFactoryPointcutAdvisor beanFactory = new DefaultBeanFactoryPointcutAdvisor();
    beanFactory.setPointcut(new GraphQLStaticMethodMatcherPointcut());
    beanFactory.setAdvice(advice);
    return beanFactory;
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplateBuilder builder = new RestTemplateBuilder();
    return builder.build();
  }
}
