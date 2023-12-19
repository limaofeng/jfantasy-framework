package org.jfantasy.graphql.gateway;

import org.jfantasy.graphql.client.GraphQLTemplate;
import org.jfantasy.graphql.gateway.service.DefaultGraphQLTemplateFactory;
import org.jfantasy.graphql.gateway.service.RemoteGraphQLService;

public interface GraphQLTemplateFactory {

  GraphQLTemplateFactory DEFAULT = new DefaultGraphQLTemplateFactory();

  GraphQLTemplate client(RemoteGraphQLService service);
}
