package org.jfantasy.graphql.directives;

import graphql.kickstart.tools.boot.SchemaDirective;
import org.springframework.context.annotation.Bean;

/**
 * GraphQL 指令配置
 *
 * @author limaofeng
 */
public class GraphQLDirectiveConfiguration {
  @Bean
  public SchemaDirective authorisationDirective() {
    return new SchemaDirective("auth", new AuthorisationDirective());
  }

  @Bean
  public SchemaDirective dateFormattingDirective() {
    return new SchemaDirective("dateFormat", new DateFormatDirective());
  }

  @Bean
  public SchemaDirective fileSizeDirective() {
    return new SchemaDirective("fileSize", new FileSizeDirective());
  }
}
