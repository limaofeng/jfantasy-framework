package net.asany.jfantasy.graphql.gateway;

import graphql.schema.GraphQLSchema;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.config.*;
import net.asany.jfantasy.graphql.gateway.data.GatewayDataFetcherFactory;
import net.asany.jfantasy.graphql.gateway.directive.DirectiveFactory;
import net.asany.jfantasy.graphql.gateway.service.GraphQLService;
import net.asany.jfantasy.graphql.gateway.service.LocalGraphQLService;
import net.asany.jfantasy.graphql.gateway.service.RemoteGraphQLService;
import net.asany.jfantasy.graphql.gateway.type.DefaultScalarTypeProvider;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProviderFactory;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeResolver;
import net.asany.jfantasy.graphql.gateway.util.GraphQLUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.PropertyUtils;

@Slf4j
@Builder(builderClassName = "Builder")
public class GraphQLGateway {

  private List<GraphQLService> serviceList;

  @Getter private GraphQLSchema schema;

  private SchemaOverride override;

  private GatewayDataFetcherFactory dataFetcherFactory;

  private DirectiveFactory directiveFactory;

  private GraphQLGateway(
      List<GraphQLService> serviceList,
      SchemaOverride override,
      GatewayDataFetcherFactory dataFetcherFactory,
      DirectiveFactory directiveFactory) {
    this.serviceList = serviceList;
    this.override = override;
    this.dataFetcherFactory = dataFetcherFactory;
    this.directiveFactory = directiveFactory;
  }

  public void init() throws IOException {
    List<GraphQLSchema> schemas = new ArrayList<>();

    for (GraphQLService service : serviceList) {
      GraphQLSchema schema = service.makeSchema();
      schemas.add(schema);
    }

    if (dataFetcherFactory == null) {
      dataFetcherFactory = new GatewayDataFetcherFactory();
    }

    if (directiveFactory == null) {
      directiveFactory = new DirectiveFactory();
    }

    directiveFactory.registerDefaultDirectives();

    this.schema =
        GraphQLUtils.mergeSchemas(schemas, this.override, dataFetcherFactory, directiveFactory);
  }

  public void destroy() {}

  public static class Builder {
    private GraphQLTemplateFactory clientFactory = GraphQLTemplateFactory.DEFAULT;
    private GraphQLSchema localSchema;
    private ScalarTypeResolver scalarResolver;
    private String config;

    private final SchemaOverride.Builder schemaOverrideBuilder = SchemaOverride.builder();

    public Builder() {
      serviceList = new ArrayList<>();
      dataFetcherFactory = new GatewayDataFetcherFactory();
      directiveFactory = new DirectiveFactory();
    }

    public Builder clientFactory(GraphQLTemplateFactory clientFactory) {
      this.clientFactory = clientFactory;
      return this;
    }

    public Builder schema(GraphQLSchema graphQLSchema) {
      this.localSchema = graphQLSchema;
      return this;
    }

    public Builder scalarResolver(ScalarTypeResolver scalarResolver) {
      this.scalarResolver = scalarResolver;
      return this;
    }

    public Builder config(String yamlPath) throws IOException {
      this.config = yamlPath;
      return this;
    }

    public Builder addService(GraphQLService service) {
      this.serviceList.add(service);
      return this;
    }

    public Builder excludeFields(String typeName, String... names) {
      schemaOverrideBuilder.excludeFields(typeName, names);
      return this;
    }

    public Builder field(String typeName, String name, String mapping) {
      schemaOverrideBuilder.field(typeName, name, mapping);
      return this;
    }

    public InputStream getConfigStream() throws FileNotFoundException {
      InputStream inputStream;
      if (this.config.startsWith("classpath:")) { // 从类路径加载资源
        String resourcePath = this.config.substring(10); // 移除 "classpath:" 前缀
        inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
          throw new FileNotFoundException("Resource " + resourcePath + " not found in classpath");
        }
      } else { // 从文件系统加载资源
        try {
          inputStream = new FileInputStream(this.config);
        } catch (FileNotFoundException e) {
          throw new FileNotFoundException(this.config + " file not found in file system");
        }
      }
      return inputStream;
    }

    @SneakyThrows
    public GraphQLGateway build() {
      // 如果没有指定 ScalarTypeResolver，则使用默认的
      if (scalarResolver == null) {
        ScalarTypeProviderFactory scalarProviderFactory = new ScalarTypeProviderFactory();
        // 注册所有已知的 ScalarTypeProvider 实现
        scalarProviderFactory.registerProvider("spring", new DefaultScalarTypeProvider());
        scalarResolver = new ScalarTypeResolver(scalarProviderFactory);
      }

      // 读取配置文件
      InputStream inputStream = getConfigStream();

      Constructor constructor = new Constructor(GatewayConfig.class);
      PropertyUtils propertyUtils = new GatewayPropertyUtils();
      propertyUtils.setSkipMissingProperties(true);
      constructor.setPropertyUtils(propertyUtils);
      Yaml yaml = new Yaml(constructor);

      //noinspection VulnerableCodeUsages
      GatewayConfig gatewayConfig = yaml.load(inputStream);

      // 注册所有的ScalarType
      scalarResolver.setScalars(gatewayConfig.getScalars());

      List<GraphQLService> services = new ArrayList<>(this.serviceList);
      for (GatewayConfig.ServiceConfig serviceConfig : gatewayConfig.getServices()) {
        RemoteGraphQLService.Builder serviceBuilder =
            RemoteGraphQLService.builder()
                .name(serviceConfig.getName())
                .url(serviceConfig.getUrl())
                .clientFactory(this.clientFactory)
                .headers(serviceConfig.getHeaders())
                .scalarTypeResolver(this.scalarResolver);
        if (serviceConfig.getExcludeFields() != null) {
          serviceBuilder.excludeFields(serviceConfig.getExcludeFields());
        }
        services.add(serviceBuilder.build());
      }

      // 如果有配置override，则添加override
      if (gatewayConfig.getOverride() != null) {
        for (GatewayConfig.OverrideConfig overrideConfig : gatewayConfig.getOverride()) {
          schemaOverrideBuilder.addType(overrideConfig.getType(), overrideConfig);
        }
      }

      // 如果有配置 dataFetchers，则添加 dataFetchers
      if (gatewayConfig.getDataFetchers() != null) {
        for (DataFetcherConfig dataFetcherConfig : gatewayConfig.getDataFetchers()) {
          dataFetcherFactory.registerDataFetcher(dataFetcherConfig);
        }
      }

      // 如果有配置 directives，则添加 directives
      if (gatewayConfig.getDirectives() != null) {
        for (DirectiveConfig directiveConfig : gatewayConfig.getDirectives()) {
          Directive directive =
              directiveFactory.registerDirective(
                  directiveConfig.getDefinition(), directiveConfig.getHandler());
          log.info("register directive: {}", directive.getName());
        }
      }

      // 如果有配置localSchema，则添加localSchema
      if (localSchema != null) {
        services.add(LocalGraphQLService.builder().schema(localSchema).build());
      }

      return new GraphQLGateway(
          services, schemaOverrideBuilder.build(), dataFetcherFactory, directiveFactory);
    }
  }
}
