/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
