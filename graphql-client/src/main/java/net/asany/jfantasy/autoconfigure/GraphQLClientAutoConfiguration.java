package net.asany.jfantasy.autoconfigure;

import net.asany.jfantasy.graphql.client.GraphQLClientBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class GraphQLClientAutoConfiguration {

  @Bean
  static GraphQLClientBeanPostProcessor clientBeanPostProcessor(
      final ApplicationContext applicationContext, final ResourceLoader resourceLoader) {
    return new GraphQLClientBeanPostProcessor(applicationContext, resourceLoader);
  }
}
