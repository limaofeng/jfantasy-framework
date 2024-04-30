package net.asany.jfantasy.graphql.gateway;

import com.fasterxml.jackson.databind.JsonSerializer;
import net.asany.jfantasy.graphql.client.GraphQLTemplate;
import net.asany.jfantasy.graphql.gateway.service.DefaultGraphQLTemplateFactory;
import net.asany.jfantasy.graphql.gateway.service.RemoteGraphQLService;

public interface GraphQLTemplateFactory {

  GraphQLTemplateFactory DEFAULT = new DefaultGraphQLTemplateFactory();

  GraphQLTemplate client(RemoteGraphQLService service);

  <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> ser);
}
