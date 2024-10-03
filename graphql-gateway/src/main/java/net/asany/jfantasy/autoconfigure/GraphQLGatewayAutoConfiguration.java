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
package net.asany.jfantasy.autoconfigure;

import graphql.execution.instrumentation.Instrumentation;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.servlet.config.GraphQLSchemaServletProvider;
import graphql.kickstart.tools.SchemaParser;
import java.util.List;
import net.asany.jfantasy.autoconfigure.properties.GatewayProperties;
import net.asany.jfantasy.autoconfigure.properties.SecurityAuthorizationProperties;
import net.asany.jfantasy.framework.security.authorization.PolicyBasedAuthorizationProvider;
import net.asany.jfantasy.framework.security.authorization.config.AuthorizationConfiguration;
import net.asany.jfantasy.framework.security.authorization.config.ConfigurationPolicyBasedAuthorizationProvider;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.security.authorization.policy.context.WebRequestContextBuilder;
import net.asany.jfantasy.graphql.gateway.GraphQLClientFactory;
import net.asany.jfantasy.graphql.gateway.GraphQLGateway;
import net.asany.jfantasy.graphql.gateway.GraphQLGatewayReloadSchemaProvider;
import net.asany.jfantasy.graphql.gateway.error.GraphQLGatewayErrorHandler;
import net.asany.jfantasy.graphql.gateway.execution.AuthInstrumentation;
import net.asany.jfantasy.graphql.gateway.service.DefaultGraphQLClientFactory;
import net.asany.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProvider;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProviderFactory;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeResolver;
import net.asany.jfantasy.graphql.gateway.type.SpringScalarTypeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({GatewayProperties.class, SecurityAuthorizationProperties.class})
public class GraphQLGatewayAutoConfiguration {

  @Bean
  public GraphQLGatewayErrorHandler graphQLGatewayErrorHandler() {
    return new GraphQLGatewayErrorHandler();
  }

  @Bean
  public GraphQLClientFactory graphQLTemplateFactory(
      ResourceLoader resourceLoader, RestTemplate restTemplate, GraphQLObjectMapper objectMapper) {
    return new DefaultGraphQLClientFactory(
        resourceLoader, restTemplate, objectMapper.getJacksonMapper());
  }

  @Bean(initMethod = "init", destroyMethod = "destroy")
  public GraphQLGateway graphqlGateway(
      SchemaParser schemaParser,
      GraphQLClientFactory templateFactory,
      ScalarTypeProviderFactory scalarFactory,
      @Autowired GatewayProperties gatewayProperties) {
    return GraphQLGateway.builder()
        .schema(schemaParser.makeExecutableSchema())
        .clientFactory(templateFactory)
        .scalarResolver(new ScalarTypeResolver(scalarFactory))
        .configLocation(gatewayProperties.getConfigLocation())
        .build();
  }

  @Bean("graphQLSchemaProvider")
  public GraphQLSchemaServletProvider graphqlSchemaProvider(GraphQLGateway graphQLGateway) {
    return new GraphQLGatewayReloadSchemaProvider(graphQLGateway);
  }

  @Bean("authorizationConfiguration")
  public AuthorizationConfiguration authorizationConfiguration(
      SecurityAuthorizationProperties properties) {
    return AuthorizationConfiguration.load(properties.getConfigLocation());
  }

  @Bean("policyBasedAuthorizationProvider")
  public PolicyBasedAuthorizationProvider policyBasedAuthorizationProvider(
      AuthorizationConfiguration configuration) {
    RequestContextFactory requestContextFactory =
        new RequestContextFactory(List.of(new WebRequestContextBuilder()));
    return new ConfigurationPolicyBasedAuthorizationProvider(requestContextFactory, configuration);
  }

  @Bean("authInstrumentation")
  public Instrumentation authInstrumentation(
      PolicyBasedAuthorizationProvider policyBasedAuthorizationProvider) {
    return new AuthInstrumentation(policyBasedAuthorizationProvider);
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

  @Bean("base.startupRunner")
  @ConditionalOnBean(GraphQLGateway.class)
  public CommandLineRunner startupRunner(
      @Autowired(required = false) GraphQLGateway graphQLGateway) {
    return args -> {
      List<RemoteGraphQLService> services =
          graphQLGateway.getGraphQLService(RemoteGraphQLService.class);
      for (RemoteGraphQLService service : services) {
        if (service.getSubscription().isEnabled()) {
          service.getClient().connect();
        }
      }
    };
  }
}
