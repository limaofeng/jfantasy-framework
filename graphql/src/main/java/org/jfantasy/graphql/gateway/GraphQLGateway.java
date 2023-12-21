package org.jfantasy.graphql.gateway;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.graphql.gateway.config.GatewayConfig;
import org.jfantasy.graphql.gateway.data.GraphQLGatewayDataFetcherFactory;
import org.jfantasy.graphql.gateway.service.GraphQLService;
import org.jfantasy.graphql.gateway.service.LocalGraphQLService;
import org.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import org.jfantasy.graphql.gateway.type.DefaultScalarTypeProvider;
import org.jfantasy.graphql.gateway.type.ScalarTypeProviderFactory;
import org.jfantasy.graphql.gateway.type.ScalarTypeResolver;
import org.jfantasy.graphql.util.GraphQLUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

@Slf4j
public class GraphQLGateway {

  private final List<GraphQLService> serviceList;

  @Getter private GraphQLSchema schema;

  @Builder
  public GraphQLGateway() {
    this.serviceList = new ArrayList<>();
  }

  public GraphQLGateway(List<GraphQLService> serviceList) {
    this.serviceList = serviceList;
  }

  public GraphQLGateway(List<GraphQLService> serviceList, List<GraphQLScalarType> scalars) {
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
    private final List<GraphQLService> serviceList = new ArrayList<>();
    private GraphQLTemplateFactory clientFactory = GraphQLTemplateFactory.DEFAULT;
    private GraphQLSchema localSchema;
    private ScalarTypeResolver scalarResolver;
    private String config;

    public GraphQLGatewayBuilder clientFactory(GraphQLTemplateFactory clientFactory) {
      this.clientFactory = clientFactory;
      return this;
    }

    public GraphQLGatewayBuilder schema(GraphQLSchema graphQLSchema) {
      this.localSchema = graphQLSchema;
      return this;
    }

    public GraphQLGatewayBuilder scalarResolver(ScalarTypeResolver scalarResolver) {
      this.scalarResolver = scalarResolver;
      return this;
    }

    public GraphQLGatewayBuilder config(String yamlPath) throws IOException {
      this.config = yamlPath;
      return this;
    }

    public GraphQLGatewayBuilder addService(GraphQLService service) {
      this.serviceList.add(service);
      return this;
    }

    @SneakyThrows
    public GraphQLGateway build() {
      // 如果没有指定ScalarTypeResolver，则使用默认的
      if (scalarResolver == null) {
        ScalarTypeProviderFactory scalarProviderFactory = new ScalarTypeProviderFactory();
        // 注册所有已知的ScalarTypeProvider实现
        scalarProviderFactory.registerProvider("spring", new DefaultScalarTypeProvider());
        scalarResolver = new ScalarTypeResolver(scalarProviderFactory);
      }

      // 读取配置文件
      InputStream inputStream = ClassLoader.getSystemResourceAsStream(this.config);

      if (inputStream == null) {
        throw new FileNotFoundException(this.config + "config file not found");
      }

      Constructor constructor = new Constructor(GatewayConfig.class);
      PropertyUtils propertyUtils = new PropertyUtils();
      propertyUtils.setSkipMissingProperties(true);
      constructor.setPropertyUtils(propertyUtils);
      Yaml yaml = new Yaml(constructor);

      @SuppressWarnings("VulnerableCodeUsages")
      GatewayConfig gatewayConfig = yaml.load(inputStream);

      // 注册所有的ScalarType
      scalarResolver.setScalars(gatewayConfig.getScalars());

      List<GraphQLService> services = new ArrayList<>(this.serviceList);
      for (GatewayConfig.ServiceConfig serviceConfig : gatewayConfig.getServices()) {
        RemoteGraphQLService.RemoteGraphQLServiceBuilder serviceBuilder =
            RemoteGraphQLService.builder()
                .name(serviceConfig.getName())
                .url(serviceConfig.getUrl())
                .clientFactory(this.clientFactory)
                .scalarTypeResolver(scalarResolver);

        // 如果有配置override，则进行override
        if (serviceConfig.getOverride() != null) {
          List<GatewayConfig.OverrideConfig> overrideConfigs = serviceConfig.getOverride();
          for (GatewayConfig.OverrideConfig overrideConfig : overrideConfigs) {

            for (GatewayConfig.FieldConfig fieldConfig : overrideConfig.getFields()) {

              // 如果ignore为true，则忽略该字段
              if (fieldConfig.getIgnore() == Boolean.TRUE) {
                serviceBuilder.ignoreField(overrideConfig.getType(), fieldConfig.getName());
              }

              // 如果rename不为空，则重命名该字段
              if (fieldConfig.getRename() != null) {
                serviceBuilder.renameField(
                    overrideConfig.getType(), fieldConfig.getName(), fieldConfig.getRename());
              }

              // 如果有配置arguments，则进行arguments的override
              if (fieldConfig.getArguments() != null) {
                for (GatewayConfig.ArgumentConfig argumentConfig : fieldConfig.getArguments()) {

                  // 如果ignore为true，则忽略该参数
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

      // 如果有配置localSchema，则添加localSchema
      if (localSchema != null) {
        services.add(LocalGraphQLService.builder().schema(localSchema).build());
      }

      return new GraphQLGateway(services);
    }
  }
}
