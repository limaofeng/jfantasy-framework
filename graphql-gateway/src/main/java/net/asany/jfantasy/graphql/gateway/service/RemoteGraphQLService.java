package net.asany.jfantasy.graphql.gateway.service;

import static graphql.schema.idl.SchemaPrinter.Options.defaultOptions;

import graphql.language.Document;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.parser.Parser;
import graphql.schema.DataFetcherFactory;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLSchema;
import graphql.schema.TypeResolver;
import graphql.schema.idl.*;
import java.io.IOException;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.graphql.gateway.GraphQLClient;
import net.asany.jfantasy.graphql.gateway.config.GatewayConfig;
import net.asany.jfantasy.graphql.gateway.data.ServiceDataFetcherFactory;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeResolver;
import net.asany.jfantasy.graphql.gateway.util.GraphQLTypeUtils;

@Slf4j
@Data
@lombok.Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class RemoteGraphQLService implements GraphQLService {
  private String name;

  private String typeDefs;
  private Document document;

  private Map<String, Set<String>> excludeFields;

  private TypeResolver defaultTypeResolver;

  private DataFetcherFactory<?> defaultDataFetcher;
  private ScalarTypeResolver scalarTypeResolver;

  private GraphQLClient client;
  private GraphQLSchema schema;

  private GatewayConfig.SubscriptionConfig subscription;
  private GatewayConfig.IntrospectionConfig introspection;

  @SneakyThrows
  private Document loadDocument() {
    if (this.introspection.isEnabled()) {
      Document document = this.client.introspectionQuery();
      this.typeDefs = new SchemaPrinter(defaultOptions().includeDirectives(false)).print(document);
      return document;
    }
    return new Parser().parseDocument(this.typeDefs);
  }

  public DataFetcherFactory<?> getDefaultDataFetcher() {
    if (this.defaultDataFetcher == null) {
      this.defaultDataFetcher = new ServiceDataFetcherFactory(this);
    }
    return defaultDataFetcher;
  }

  public TypeResolver getDefaultTypeResolver() {
    if (this.defaultTypeResolver == null) {
      this.defaultTypeResolver = new GraphQLServiceTypeResolver(this);
    }
    return this.defaultTypeResolver;
  }

  public GraphQLSchema getSchema() {
    if (this.schema == null) {
      try {
        this.makeSchema();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return this.schema;
  }

  @SneakyThrows
  public GraphQLSchema makeSchema() throws IOException {
    this.document = this.loadDocument();

    SchemaPrinter.Options noDirectivesOption = defaultOptions().includeDirectives(false);

    TypeDefinitionRegistry typeRegistry =
        new SchemaParser().parse(new SchemaPrinter(noDirectivesOption).print(this.document));

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    RuntimeWiring.Builder runtimeWiringBuilder = RuntimeWiring.newRuntimeWiring();

    Map<String, ScalarTypeDefinition> scalars = typeRegistry.scalars();

    for (String scalarName : scalars.keySet()) {
      if (GraphQLTypeUtils.hasBaseScalar(scalarName)) {
        continue;
      }
      runtimeWiringBuilder.scalar(scalarTypeResolver.resolveScalarType(scalarName));
    }

    List<InterfaceTypeDefinition> interfaceTypes =
        typeRegistry.getTypes(InterfaceTypeDefinition.class);
    for (InterfaceTypeDefinition interfaceType : interfaceTypes) {
      runtimeWiringBuilder.type(
          TypeRuntimeWiring.newTypeWiring(interfaceType.getName())
              .typeResolver(this.getDefaultTypeResolver()));
    }

    GraphQLCodeRegistry.Builder codeRegistryBuilder = GraphQLCodeRegistry.newCodeRegistry();
    codeRegistryBuilder.defaultDataFetcher(this.getDefaultDataFetcher());
    runtimeWiringBuilder.codeRegistry(codeRegistryBuilder.build());

    for (Map.Entry<String, Set<String>> entry : this.excludeFields.entrySet()) {
      String typeName = entry.getKey();
      Set<String> fields = entry.getValue();

      Optional<ObjectTypeDefinition> typeDefinitionOptional =
          typeRegistry.getType(typeName, ObjectTypeDefinition.class);
      if (typeDefinitionOptional.isEmpty()) {
        continue;
      }
      ObjectTypeDefinition typeDefinition = typeDefinitionOptional.get();
      ObjectTypeDefinition.Builder newTypeDefinitionBuilder =
          ObjectTypeDefinition.newObjectTypeDefinition()
              .name(typeDefinition.getName())
              .description(typeDefinition.getDescription())
              .implementz(typeDefinition.getImplements())
              .directives(typeDefinition.getDirectives())
              .fieldDefinitions(
                  typeDefinition.getFieldDefinitions().stream()
                      .filter(fieldDefinition -> !fields.contains(fieldDefinition.getName()))
                      .toList());

      typeRegistry.remove(typeDefinition);
      typeRegistry.add(newTypeDefinitionBuilder.build());
    }

    this.schema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiringBuilder.build());

    return this.schema;
  }

  public static class Builder {
    public Builder() {
      this.excludeFields = new HashMap<>();
    }

    public Builder excludeFields(List<String> fields) {
      if (this.excludeFields == null) {
        this.excludeFields = new HashMap<>();
      }
      for (String field : fields) {
        String typeName = field.split("\\.")[0];
        String fieldName = field.split("\\.")[1];
        if (!this.excludeFields.containsKey(typeName)) {
          this.excludeFields.put(typeName, Set.of(fieldName));
        } else {
          this.excludeFields.get(typeName).add(fieldName);
        }
      }
      return this;
    }

    public Builder excludeFields(String... fields) {
      return excludeFields(List.of(fields));
    }
  }
}
