package net.asany.jfantasy.autoconfigure;

import java.util.List;
import net.asany.jfantasy.graphql.gateway.GraphQLGateway;
import net.asany.jfantasy.graphql.gateway.error.GraphQLGatewayErrorHandler;
import net.asany.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProvider;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProviderFactory;
import net.asany.jfantasy.graphql.gateway.type.SpringScalarTypeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLGatewayAutoConfiguration {

  @Bean
  public GraphQLGatewayErrorHandler graphQLGatewayErrorHandler() {
    return new GraphQLGatewayErrorHandler();
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
