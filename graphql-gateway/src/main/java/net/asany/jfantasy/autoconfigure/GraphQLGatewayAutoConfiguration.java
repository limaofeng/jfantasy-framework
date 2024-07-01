package net.asany.jfantasy.autoconfigure;

import com.fasterxml.jackson.annotation.JsonInclude;
import graphql.execution.instrumentation.Instrumentation;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.servlet.config.GraphQLSchemaServletProvider;
import graphql.kickstart.tools.SchemaParser;
import java.io.IOException;
import java.util.List;
import net.asany.jfantasy.autoconfigure.properties.GatewayProperties;
import net.asany.jfantasy.framework.security.authorization.AuthorizationService;
import net.asany.jfantasy.framework.security.authorization.policy.config.ConfigurationPermissionPolicyManager;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.security.authorization.policy.context.WebRequestContextBuilder;
import net.asany.jfantasy.graphql.gateway.GraphQLClientFactory;
import net.asany.jfantasy.graphql.gateway.GraphQLGateway;
import net.asany.jfantasy.graphql.gateway.GraphQLGatewayReloadSchemaProvider;
import net.asany.jfantasy.graphql.gateway.error.GraphQLGatewayErrorHandler;
import net.asany.jfantasy.graphql.gateway.execution.AuthInstrumentation;
import net.asany.jfantasy.graphql.gateway.security.GraphQLAuthorizationService;
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
@EnableConfigurationProperties({GatewayProperties.class})
public class GraphQLGatewayAutoConfiguration {

  @Bean
  public GraphQLGatewayErrorHandler graphQLGatewayErrorHandler() {
    return new GraphQLGatewayErrorHandler();
  }

  @Bean
  public GraphQLClientFactory graphQLTemplateFactory(
      ResourceLoader resourceLoader, RestTemplate restTemplate, GraphQLObjectMapper objectMapper) {
    return new DefaultGraphQLClientFactory(
        resourceLoader,
        restTemplate,
        objectMapper
            .getJacksonMapper()
            .copy()
            .setSerializationInclusion(JsonInclude.Include.ALWAYS));
  }

  @Bean(initMethod = "init", destroyMethod = "destroy")
  public GraphQLGateway graphqlGateway(
      SchemaParser schemaParser,
      GraphQLClientFactory templateFactory,
      ScalarTypeProviderFactory scalarFactory,
      @Autowired GatewayProperties gatewayProperties)
      throws IOException {
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

  @Bean("authInstrumentation")
  public Instrumentation authInstrumentation(AuthorizationService authorizationService) {
    return new AuthInstrumentation(authorizationService);
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

  @Bean
  public AuthorizationService authorizationService() {
    RequestContextFactory requestContextFactory =
        new RequestContextFactory(List.of(new WebRequestContextBuilder()));

    net.asany.jfantasy.framework.security.authorization.policy.config.Configuration configuration =
        net.asany.jfantasy.framework.security.authorization.policy.config.Configuration.load(
            "/Users/limaofeng/Workspace/framework/graphql-gateway/src/test/resources/auth-policy.yaml");

    ConfigurationPermissionPolicyManager permissionPolicyManager =
        new ConfigurationPermissionPolicyManager(configuration);
    return new GraphQLAuthorizationService(permissionPolicyManager, requestContextFactory);
  }
}
