package net.asany.jfantasy.autoconfigure;

import java.util.List;
import net.asany.jfantasy.graphql.gateway.error.GraphQLGatewayErrorHandler;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProvider;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProviderFactory;
import net.asany.jfantasy.graphql.gateway.type.SpringScalarTypeProvider;
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
}
