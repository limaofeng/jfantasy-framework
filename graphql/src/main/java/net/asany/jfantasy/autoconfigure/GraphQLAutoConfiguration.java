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

import graphql.kickstart.autoconfigure.tools.GraphQLJavaToolsAutoConfiguration;
import graphql.kickstart.tools.SchemaParserDictionary;
import java.util.List;
import net.asany.jfantasy.graphql.SchemaParserDictionaryBuilder;
import net.asany.jfantasy.graphql.context.DataLoaderRegistryCustomizer;
import net.asany.jfantasy.graphql.error.GraphQLResolverAdvice;
import net.asany.jfantasy.graphql.error.GraphqlStaticMethodMatcherPointcut;
import net.asany.jfantasy.graphql.error.TokenGraphQLServletListener;
import org.dataloader.DataLoaderRegistry;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * GraphQL 自动配置
 *
 * @author limaofeng
 * @version V1.0
 */
@Configuration
@AutoConfigureBefore(GraphQLJavaToolsAutoConfiguration.class)
@ComponentScan({"net.asany.jfantasy.graphql.context", "net.asany.jfantasy.graphql.error"})
public class GraphQLAutoConfiguration {

  @Bean
  public TokenGraphQLServletListener tokenGraphQLServletListener() {
    return new TokenGraphQLServletListener();
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
}
