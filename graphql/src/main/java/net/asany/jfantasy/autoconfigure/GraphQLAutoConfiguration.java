package net.asany.jfantasy.autoconfigure;

import graphql.execution.ExecutionStrategy;
import graphql.kickstart.autoconfigure.tools.GraphQLJavaToolsAutoConfiguration;
import graphql.kickstart.autoconfigure.web.servlet.GraphQLWebAutoConfiguration;
import graphql.kickstart.tools.SchemaParserDictionary;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.HandshakeRequest;
import java.util.List;
import net.asany.jfantasy.framework.security.authentication.AuthenticationManagerResolver;
import net.asany.jfantasy.graphql.SchemaParserDictionaryBuilder;
import net.asany.jfantasy.graphql.client.GraphQLClientBeanPostProcessor;
import net.asany.jfantasy.graphql.context.DataLoaderRegistryCustomizer;
import net.asany.jfantasy.graphql.context.SecurityGraphQLContextBuilder;
import net.asany.jfantasy.graphql.error.GraphQLResolverAdvice;
import net.asany.jfantasy.graphql.error.GraphqlStaticMethodMatcherPointcut;
import net.asany.jfantasy.graphql.error.TokenGraphQLServletListener;
import net.asany.jfantasy.graphql.execution.AsyncMutationExecutionStrategy;
import net.asany.jfantasy.graphql.execution.AsyncQueryExecutionStrategy;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProvider;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProviderFactory;
import net.asany.jfantasy.graphql.gateway.type.SpringScalarTypeProvider;
import org.dataloader.DataLoaderRegistry;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * GraphQL 自动配置
 *
 * @author limaofeng
 * @version V1.0
 */
@Configuration
@AutoConfigureBefore(GraphQLJavaToolsAutoConfiguration.class)
@AutoConfigureAfter(OAuth2SecurityAutoConfiguration.class)
@ComponentScan({"net.asany.jfantasy.graphql.context", "net.asany.jfantasy.graphql.error"})
public class GraphQLAutoConfiguration {

  @Bean
  static GraphQLClientBeanPostProcessor clientBeanPostProcessor(
      final ApplicationContext applicationContext, final ResourceLoader resourceLoader) {
    return new GraphQLClientBeanPostProcessor(applicationContext, resourceLoader);
  }

  @Bean
  @ConditionalOnClass(EnableWebMvc.class)
  public SecurityGraphQLContextBuilder securityGraphQLContextBuilder(
      AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver,
      AuthenticationManagerResolver<HandshakeRequest> websocketAuthenticationManagerResolver,
      DataLoaderRegistry dataLoaderRegistry) {
    return new SecurityGraphQLContextBuilder(
        authenticationManagerResolver, websocketAuthenticationManagerResolver, dataLoaderRegistry);
  }

  @Bean
  public TokenGraphQLServletListener tokenGraphQLServletListener() {
    return new TokenGraphQLServletListener();
  }

  @Bean(GraphQLWebAutoConfiguration.QUERY_EXECUTION_STRATEGY)
  @ConditionalOnMissingBean(name = GraphQLWebAutoConfiguration.QUERY_EXECUTION_STRATEGY)
  public ExecutionStrategy queryExecutionStrategy() {
    return new AsyncQueryExecutionStrategy();
  }

  @Bean(GraphQLWebAutoConfiguration.MUTATION_EXECUTION_STRATEGY)
  @ConditionalOnMissingBean(name = GraphQLWebAutoConfiguration.MUTATION_EXECUTION_STRATEGY)
  public ExecutionStrategy mutationExecutionStrategy() {
    return new AsyncMutationExecutionStrategy();
  }

  @Bean
  public SchemaParserDictionary schemaParserDictionary(
      List<SchemaParserDictionaryBuilder> builders) {
    SchemaParserDictionary dictionary = new SchemaParserDictionary();
    builders.forEach(item -> item.build(dictionary));
    return dictionary;
  }

  @Bean
  public DefaultBeanFactoryPointcutAdvisor graphqlErrorPointcutAdvisor(
      @Autowired GraphQLResolverAdvice advice) {
    DefaultBeanFactoryPointcutAdvisor beanFactory = new DefaultBeanFactoryPointcutAdvisor();
    beanFactory.setPointcut(new GraphqlStaticMethodMatcherPointcut());
    beanFactory.setAdvice(advice);
    return beanFactory;
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplateBuilder builder = new RestTemplateBuilder();
    return builder.build();
  }

  @Bean
  public DataLoaderRegistry dataLoaderRegistry(List<DataLoaderRegistryCustomizer> customizers) {
    DataLoaderRegistry registry = DataLoaderRegistry.newRegistry().build();
    for (DataLoaderRegistryCustomizer customizer : customizers) {
      customizer.customize(registry);
    }
    return registry;
  }

  @Bean
  public ScalarTypeProviderFactory scalarTypeProviderFactory(List<ScalarTypeProvider> providers) {
    ScalarTypeProviderFactory providerFactory = new ScalarTypeProviderFactory();
    for (ScalarTypeProvider provider : providers) {
      providerFactory.registerProvider(provider.getName(), provider);
    }
    return providerFactory;
  }

  @Bean
  public ScalarTypeProvider springScalarTypeProvider() {
    return new SpringScalarTypeProvider();
  }
}
