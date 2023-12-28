package net.asany.jfantasy.graphql.gateway.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder(builderClassName = "Builder")
public class SchemaOverrideType {
  private String type;
  private String mapping;
  private Map<String, SchemaOverrideField> fields;

  public void excludeField(String name) {
    if (!this.fields.containsKey(name)) {
      this.fields.put(name, SchemaOverrideField.builder().name(name).build());
    }
    SchemaOverrideField field = this.fields.get(name);
    field.setExclude(true);
  }

  public void renameField(String name, String mapping) {
    if (!this.fields.containsKey(name)) {
      this.fields.put(name, SchemaOverrideField.builder().name(name).build());
    }
    SchemaOverrideField field = this.fields.get(name);
    field.setMapping(mapping);
    //    this.newFields.put(newName, field);
  }

  public void renameFieldArgument(String fieldName, String name, String newName) {
    if (!this.fields.containsKey(fieldName)) {
      this.fields.put(fieldName, SchemaOverrideField.builder().name(fieldName).build());
    }
    SchemaOverrideField field = this.fields.get(fieldName);
    field.renameArgument(name, newName);
  }

  public boolean hasIncludeField(String name) {
    return this.fields.containsKey(name) && !this.fields.get(name).isExclude();
  }

  public SchemaOverrideField getField(String name) {
    return this.fields.get(name);
  }

  public static class Builder {

    public Builder() {
      this.fields = new HashMap<>();
    }

    public void addField(SchemaOverrideField field) {
      this.fields.put(field.getName(), field);
    }
  }
}
