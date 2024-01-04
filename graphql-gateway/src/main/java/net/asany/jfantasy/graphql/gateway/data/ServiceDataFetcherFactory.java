package net.asany.jfantasy.graphql.gateway.data;

import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.service.RemoteGraphQLService;

@Slf4j
public class ServiceDataFetcherFactory implements DataFetcherFactory<Object> {

  private final RemoteGraphQLService service;

  public ServiceDataFetcherFactory(RemoteGraphQLService service) {
    this.service = service;
  }

  @Override
  public DataFetcher<Object> get(DataFetcherFactoryEnvironment environment) {
    return new ServiceDataFetcher(service);
  }
}
