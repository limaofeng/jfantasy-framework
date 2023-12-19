package org.jfantasy.graphql.gateway;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.jfantasy.graphql.gateway.config.GatewayConfig;
import org.jfantasy.graphql.gateway.data.GraphQLGatewayDataFetcherFactory;
import org.jfantasy.graphql.gateway.service.GraphQLService;
import org.jfantasy.graphql.gateway.service.LocalGraphQLService;
import org.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import org.jfantasy.graphql.util.GraphQLUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

public class GraphQLGateway {

  private final List<GraphQLService> serviceList;

  @Getter private GraphQLSchema schema;

  @Builder
  public GraphQLGateway() {
    this.serviceList = new ArrayList<>();
  }

  @Builder
  public GraphQLGateway(List<GraphQLService> serviceList) {
    this.serviceList = serviceList;
  }

  public void init() throws IOException {
    GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();
    List<GraphQLSchema> schemas = new ArrayList<>();

    for (GraphQLService service : serviceList) {
      GraphQLSchema schema = service.makeSchema();

      schemas.add(schema);
    }

    codeRegistryBuilder.defaultDataFetcher(new GraphQLGatewayDataFetcherFactory());

    this.schema = GraphQLUtils.mergeSchemas(schemas, codeRegistryBuilder.build());
  }

  public void destroy() {}

  public static class GraphQLGatewayBuilder {
    private List<GraphQLService> serviceList;
    private GraphQLTemplateFactory clientFactory = GraphQLTemplateFactory.DEFAULT;
    private GraphQLSchema localSchema;

    public GraphQLGatewayBuilder clientFactory(GraphQLTemplateFactory clientFactory) {
      this.clientFactory = clientFactory;
      return this;
    }

    public GraphQLGatewayBuilder schema(GraphQLSchema graphQLSchema) {
      this.localSchema = graphQLSchema;
      return this;
    }

    public GraphQLGatewayBuilder load(String yamlPath) throws IOException {
      // 读取配置文件
      InputStream inputStream = ClassLoader.getSystemResourceAsStream(yamlPath);

      Constructor constructor = new Constructor(GatewayConfig.class);
      PropertyUtils propertyUtils = new PropertyUtils();
      propertyUtils.setSkipMissingProperties(true);
      constructor.setPropertyUtils(propertyUtils);
      Yaml yaml = new Yaml(constructor);

      @SuppressWarnings("VulnerableCodeUsages")
      GatewayConfig gatewayConfig = yaml.load(inputStream);

      List<GraphQLService> services = new ArrayList<>();

      for (GatewayConfig.ServiceConfig serviceConfig : gatewayConfig.getServices()) {
        RemoteGraphQLService.RemoteGraphQLServiceBuilder serviceBuilder =
            RemoteGraphQLService.builder()
                .name(serviceConfig.getName())
                .url(serviceConfig.getUrl())
                .clientFactory(this.clientFactory);

        if (serviceConfig.getOverride() != null) {
          List<GatewayConfig.OverrideConfig> overrideConfigs = serviceConfig.getOverride();
          for (GatewayConfig.OverrideConfig overrideConfig : overrideConfigs) {

            for (GatewayConfig.FieldConfig fieldConfig : overrideConfig.getFields()) {
              if (fieldConfig.getIgnore() == Boolean.TRUE) {
                serviceBuilder.ignoreField(overrideConfig.getType(), fieldConfig.getName());
              }
              if (fieldConfig.getRename() != null) {
                serviceBuilder.renameField(
                    overrideConfig.getType(), fieldConfig.getName(), fieldConfig.getRename());
              }
              if (fieldConfig.getArguments() != null) {
                for (GatewayConfig.ArgumentConfig argumentConfig : fieldConfig.getArguments()) {
                  if (argumentConfig.getRename() != null) {
                    serviceBuilder.renameFieldArgument(
                        overrideConfig.getType(),
                        fieldConfig.getName(),
                        argumentConfig.getName(),
                        argumentConfig.getRename());
                  }
                }
              }
            }
          }
        }

        services.add(serviceBuilder.build());
      }

      this.serviceList = services;
      return this;
    }

    public GraphQLGateway build() {
      List<GraphQLService> services = new ArrayList<>(this.serviceList);
      if (localSchema != null) {
        services.add(LocalGraphQLService.builder().schema(localSchema).build());
      }
      return new GraphQLGateway(services);
    }
  }
}
