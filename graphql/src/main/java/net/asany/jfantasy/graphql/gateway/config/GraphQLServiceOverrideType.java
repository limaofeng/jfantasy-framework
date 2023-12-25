package net.asany.jfantasy.graphql.gateway.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
public class GraphQLServiceOverrideType {
  private String type;
  private String rename;
  @Builder.Default private Map<String, GraphQLServiceOverrideField> fields = new HashMap<>();
  @Builder.Default private Map<String, GraphQLServiceOverrideField> newFields = new HashMap<>();

  public void addIgnoreField(String name) {
    if (!this.fields.containsKey(name)) {
      this.fields.put(name, GraphQLServiceOverrideField.builder().name(name).build());
    }
    GraphQLServiceOverrideField field = this.fields.get(name);
    field.setIgnore(true);
  }

  public void renameField(String name, String newName) {
    if (!this.fields.containsKey(name)) {
      this.fields.put(name, GraphQLServiceOverrideField.builder().name(name).build());
    }
    GraphQLServiceOverrideField field = this.fields.get(name);
    field.setRename(newName);
    this.newFields.put(newName, field);
  }

  public void renameFieldArgument(String fieldName, String name, String newName) {
    if (!this.fields.containsKey(fieldName)) {
      this.fields.put(fieldName, GraphQLServiceOverrideField.builder().name(fieldName).build());
    }
    GraphQLServiceOverrideField field = this.fields.get(fieldName);
    field.renameArgument(name, newName);
  }
}
