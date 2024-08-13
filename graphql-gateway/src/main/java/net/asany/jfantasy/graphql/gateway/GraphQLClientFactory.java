package net.asany.jfantasy.graphql.gateway;

import com.fasterxml.jackson.databind.JsonSerializer;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;

public interface GraphQLClientFactory {

  GraphQLClient client(GatewayConfig.ServiceConfig service);

  <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> ser);
}
