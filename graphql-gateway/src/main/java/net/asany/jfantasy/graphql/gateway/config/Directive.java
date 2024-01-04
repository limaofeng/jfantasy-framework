package net.asany.jfantasy.graphql.gateway.config;

import graphql.language.*;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.graphql.gateway.directive.ClientDirectiveHandler;

@Data
@Builder(builderClassName = "Builder")
public class Directive {

  private String name;

  private String definitionSource;

  private DirectiveDefinition definition;

  private ClientDirectiveHandler handler;
}
