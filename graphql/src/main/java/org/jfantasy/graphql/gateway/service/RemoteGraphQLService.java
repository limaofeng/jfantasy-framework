package org.jfantasy.graphql.gateway.service;

import graphql.introspection.IntrospectionQueryBuilder;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeDefinition;
import graphql.schema.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.graphql.client.GraphQLResponse;
import org.jfantasy.graphql.client.GraphQLTemplate;
import org.jfantasy.graphql.gateway.GraphQLTemplateFactory;
import org.jfantasy.graphql.gateway.config.GraphQLServiceOverride;
import org.jfantasy.graphql.gateway.config.GraphQLServiceOverrideType;
import org.jfantasy.graphql.util.GraphQLTypeUtils;
import org.springframework.http.HttpHeaders;

@Slf4j
@Data
@Builder
public class RemoteGraphQLService implements GraphQLService {
  private String name;
  private String url;

  private Map<String, String> introspectionHeaders;
  private Map<String, String> headers;

  private GraphQLServiceOverride override;

  @Setter private Document document;

  @Builder.Default private Map<String, GraphQLType> typeMap = new HashMap<>();

  private TypeResolver defaultTypeResolver;

  private DataFetcherFactory<?> defaultDataFetcher;

  private GraphQLTemplateFactory clientFactory;
  private GraphQLTemplate client;
  private GraphQLSchema schema;

  public Document introspectionQuery() throws IOException {
    GraphQLTemplate client = getClient();

    if (this.introspectionHeaders != null) {
      HttpHeaders headers = new HttpHeaders();
      this.introspectionHeaders.forEach(headers::add);
      client = client.withHeaders(headers);
    }

    String introspectionQuery =
        IntrospectionQueryBuilder.build(
            IntrospectionQueryBuilder.Options.defaultOptions()
                .inputValueDeprecation(false)
                .isOneOf(false));

    GraphQLResponse response = client.post(introspectionQuery, "IntrospectionQuery");

    @SuppressWarnings("unchecked")
    Map<String, Object> introspectionResult = response.get("$.data", HashMap.class);
    return new IntrospectionResultToSchema().createSchemaDefinition(introspectionResult);
  }

  public GraphQLTemplate getClient() {
    if (this.client == null) {
      this.client = clientFactory.client(this);
    }
    return this.client;
  }

  public ObjectTypeDefinition getObjectTypeDefinition(String name) {
    return (ObjectTypeDefinition) ObjectUtil.find(document.getDefinitions(), "name", name);
  }

  public TypeDefinition<?> getTypeDefinition(String name) {
    return (TypeDefinition<?>) ObjectUtil.find(document.getDefinitions(), "name", name);
  }

  public GraphQLObjectType getObjectType(String name) {
    return (GraphQLObjectType) typeMap.get(name);
  }

  public GraphQLOutputType getOutputType(String name) {
    return (GraphQLOutputType) typeMap.get(name);
  }

  public void addType(String name, GraphQLType type) {
    this.typeMap.put(name, type);
  }

  public boolean hasType(String name) {
    return typeMap.containsKey(name);
  }

  public DataFetcherFactory<?> getDefaultDataFetcher() {
    if (this.defaultDataFetcher == null) {
      this.defaultDataFetcher = new GraphQLServiceDataFetcherFactory(this);
    }
    return defaultDataFetcher;
  }

  public TypeResolver getDefaultTypeResolver() {
    if (this.defaultTypeResolver == null) {
      this.defaultTypeResolver = new GraphQLServiceTypeResolver(this);
    }
    return this.defaultTypeResolver;
  }

  public GraphQLInputType getInputType(String name) {
    return (GraphQLInputType) typeMap.get(name);
  }

  public GraphQLSchema getSchema() {
    if (this.schema == null) {
      try {
        this.schema = this.makeSchema();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return this.schema;
  }

  public GraphQLSchema makeSchema() throws IOException {
    this.document = this.introspectionQuery();

    ObjectTypeDefinition queryType = this.getObjectTypeDefinition("Query");
    GraphQLTypeUtils.buildObjectType(queryType, this);

    ObjectTypeDefinition mutationType = this.getObjectTypeDefinition("Mutation");
    if (mutationType != null) {
      GraphQLTypeUtils.buildObjectType(mutationType, this);
    }

    ObjectTypeDefinition subscriptionType = this.getObjectTypeDefinition("Subscription");
    if (subscriptionType != null) {
      GraphQLTypeUtils.buildObjectType(subscriptionType, this);
    }

    GraphQLObjectType query = this.getObjectType("Query");
    GraphQLObjectType mutation = this.getObjectType("Mutation");

    GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();

    codeRegistryBuilder.defaultDataFetcher(this.getDefaultDataFetcher());

    for (Definition<?> definition : this.document.getDefinitions()) {

      String name = ClassUtil.getValue(definition, "name");
      if (this.hasType(name)) {
        continue;
      }
      if (!(definition instanceof ObjectTypeDefinition)) {
        continue;
      }
      GraphQLTypeUtils.buildObjectType((ObjectTypeDefinition) definition, this);
    }

    List<?> missingTypes =
        this.document.getDefinitions().stream()
            .filter(
                d -> {
                  String name = ClassUtil.getValue(d, "name");
                  return !typeMap.containsKey(name);
                })
            .toList();

    if (!missingTypes.isEmpty()) {
      missingTypes.forEach(definition -> log.warn("missing type: {}", definition));
    }

    this.typeMap.forEach(
        (key, val) -> codeRegistryBuilder.typeResolver(key, this.getDefaultTypeResolver()));

    GraphQLSchema.Builder schemaBuilder =
        GraphQLSchema.newSchema()
            .description("The Star Wars API")
            .query(query)
            .mutation(mutation)
            //        .subscription(subscription)
            //        .additionalTypes(dictionary)
            //        .additionalDirectives(directives)
            .codeRegistry(codeRegistryBuilder.build());

    if (mutation != null) {
      schemaBuilder.mutation(mutation);
    }

    return this.schema = schemaBuilder.build();
  }

  public Optional<GraphQLServiceOverrideType> getOverrideConfigForType(String typeName) {
    return override.getOverrideConfigForType(typeName);
  }

  public GraphQLServiceOverride getOverrideConfig() {
    return this.override;
  }

  @SuppressWarnings("unused")
  public static class RemoteGraphQLServiceBuilder {

    public RemoteGraphQLService.RemoteGraphQLServiceBuilder addIntrospectionHeader(
        String name, String value) {
      if (introspectionHeaders == null) {
        introspectionHeaders = new HashMap<>();
      }
      introspectionHeaders.put(name, value);
      return this;
    }

    public RemoteGraphQLService.RemoteGraphQLServiceBuilder addHeader(String name, String value) {
      if (headers == null) {
        headers = new HashMap<>();
      }
      headers.put(name, value);
      return this;
    }

    public RemoteGraphQLService.RemoteGraphQLServiceBuilder ignoreField(
        String typeName, String name) {
      GraphQLServiceOverrideType overrideType = getOverrideType(typeName);
      overrideType.addIgnoreField(name);
      return this;
    }

    public RemoteGraphQLService.RemoteGraphQLServiceBuilder renameField(
        String typeName, String name, String newName) {
      GraphQLServiceOverrideType overrideType = getOverrideType(typeName);
      overrideType.renameField(name, newName);
      return this;
    }

    public RemoteGraphQLService.RemoteGraphQLServiceBuilder renameFieldArgument(
        String typeName, String fieldName, String name, String newName) {
      GraphQLServiceOverrideType overrideType = getOverrideType(typeName);
      overrideType.renameFieldArgument(fieldName, name, newName);
      return this;
    }

    private GraphQLServiceOverrideType getOverrideType(String typeName) {
      if (this.override == null) {
        this.override = GraphQLServiceOverride.builder().build();
      }
      return this.override
          .getOverrideConfigForType(typeName)
          .orElseGet(() -> this.override.createOverrideConfig(typeName));
    }

    public RemoteGraphQLService.RemoteGraphQLServiceBuilder ignoreField(String name) {
      String typeName = name.split("\\.")[0];
      String fieldName = name.split("\\.")[1];
      return ignoreField(typeName, fieldName);
    }
  }
}
