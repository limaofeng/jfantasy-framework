package net.asany.jfantasy.graphql.gateway.config;

import java.util.*;
import lombok.Builder;

@Builder(builderClassName = "Builder")
public class SchemaOverride {

  Map<String, SchemaOverrideType> overrides;

  public Optional<SchemaOverrideType> getOverrideConfigForType(String typeName) {
    return Optional.ofNullable(overrides.get(typeName));
  }

  public boolean hasIncludeField(String typeName, String fieldName) {
    Optional<SchemaOverrideType> overrideType = getOverrideConfigForType(typeName);
    return overrideType
        .map(type -> type.getFields().get(fieldName))
        .map(SchemaOverrideField::isExclude)
        .map(exclude -> !exclude)
        .orElse(false);
  }

  public SchemaOverrideField getField(String typeName, String fieldName) {
    Optional<SchemaOverrideType> overrideType = getOverrideConfigForType(typeName);
    return overrideType
        .map(type -> type.getFields().get(fieldName))
        .orElseThrow(() -> new RuntimeException("Field not found"));
  }

  public List<SchemaOverrideType> getTypes() {
    return new ArrayList<>(this.overrides.values());
  }

  public static class Builder {

    public Builder() {
      this.overrides = new HashMap<>();
    }

    private SchemaOverrideType getOverrideType(String typeName) {
      return this.overrides.computeIfAbsent(
          typeName, key -> SchemaOverrideType.builder().type(typeName).build());
    }

    public Builder excludeFields(String typeName, String... names) {
      SchemaOverrideType overrideType = getOverrideType(typeName);
      for (String name : names) {
        overrideType.excludeField(name);
      }
      return this;
    }

    public Builder field(String typeName, String name, String mapping) {
      SchemaOverrideType overrideType = getOverrideType(typeName);
      overrideType.renameField(name, mapping);
      return this;
    }

    public void addType(String name, GatewayConfig.OverrideConfig overrideConfig) {
      SchemaOverrideType.Builder overrideTypeBuilder =
          SchemaOverrideType.builder()
              .type(overrideConfig.getType())
              .mapping(overrideConfig.getType());

      if (overrideConfig.getMapping() != null) {
        overrideTypeBuilder.mapping(overrideConfig.getMapping());
      }

      for (GatewayConfig.FieldConfig fieldConfig : overrideConfig.getFields()) {

        SchemaOverrideField.Builder overrideFieldBuilder =
            SchemaOverrideField.builder()
                .name(fieldConfig.getName())
                .mapping(fieldConfig.getName())
                .dataFetcher(fieldConfig.getDataFetcher())
                .resolve(fieldConfig.getResolve())
                .exclude(fieldConfig.getExclude() == Boolean.TRUE);

        if (fieldConfig.getType() != null) {
          overrideFieldBuilder.type(fieldConfig.getType());
        }

        // 如果rename不为空，则重命名该字段
        if (fieldConfig.getMapping() != null) {
          overrideFieldBuilder.mapping(fieldConfig.getMapping());
        }

        // 如果有配置arguments，则进行arguments的override
        if (fieldConfig.getArguments() != null) {
          for (GatewayConfig.ArgumentConfig argumentConfig : fieldConfig.getArguments()) {
            if (argumentConfig.getExclude() == Boolean.TRUE) {
              overrideFieldBuilder.excludeArgument(
                  argumentConfig.getName(), argumentConfig.getValue());
            } else {
              overrideFieldBuilder.argument(
                  argumentConfig.getName(),
                  argumentConfig.getMapping(),
                  argumentConfig.getDefaultValue());
            }
          }
        }

        overrideTypeBuilder.addField(overrideFieldBuilder.build());
      }

      this.overrides.put(name, overrideTypeBuilder.build());
    }
  }
}
