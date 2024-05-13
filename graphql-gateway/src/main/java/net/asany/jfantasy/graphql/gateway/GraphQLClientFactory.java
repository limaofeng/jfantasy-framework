package net.asany.jfantasy.graphql.gateway;

import com.fasterxml.jackson.databind.JsonSerializer;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;
import net.asany.jfantasy.graphql.gateway.service.DefaultGraphQLClientFactory;

public interface GraphQLClientFactory {

  GraphQLClientFactory DEFAULT = new DefaultGraphQLClientFactory();

  GraphQLClient client(GatewayConfig.ServiceConfig service);

  <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> ser);
}
