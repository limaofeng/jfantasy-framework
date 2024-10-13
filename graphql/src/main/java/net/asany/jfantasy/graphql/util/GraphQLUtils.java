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
package net.asany.jfantasy.graphql.util;

import graphql.kickstart.tools.SchemaParser;
import graphql.language.Field;
import graphql.language.TypeDefinition;
import graphql.schema.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.error.UnsupportedException;

public class GraphQLUtils {

  public static <T extends TypeDefinition<?>> T getTypeDefinition(Class<?> clazz) {
    SchemaParser schemaParser = SpringBeanUtils.getBeanByType(SchemaParser.class);
    Map<TypeDefinition<?>, Type> dictionary = ClassUtil.getFieldValue(schemaParser, "dictionary");
    for (Map.Entry<TypeDefinition<?>, Type> entry : dictionary.entrySet()) {
      if (entry.getValue().equals(clazz)) {
        //noinspection unchecked
        return (T) entry.getKey();
      }
    }
    return null;
  }

  public static <T extends GraphQLSchemaElement> T getFieldDefinition(
      String typeName, String name) {
    GraphQLSchema schema = SpringBeanUtils.getBeanByType(GraphQLSchema.class);
    GraphQLType typeDefinition = schema.getType(typeName);
    assert typeDefinition != null;

    List<GraphQLSchemaElement> children = typeDefinition.getChildren();

    for (GraphQLSchemaElement element : children) {
      if (element instanceof GraphQLInputObjectField field) {
        if (field.getName().equals(name)) {
          //noinspection unchecked
          return (T) field;
        }
      }
      if (element instanceof GraphQLFieldDefinition field) {
        if (field.getName().equals(name)) {
          //noinspection unchecked
          return (T) field;
        }
      }
    }

    throw new UnsupportedException("Field not found");
  }

  public static String getExecutionStepInfoPath(DataFetchingEnvironment environment) {
    return environment.getExecutionStepInfo().getPath().toString();
  }

  public static boolean hasFetchFields(DataFetchingEnvironment environment, String... paths) {
    List<Field> fields = environment.getExecutionStepInfo().getField().getFields();
    String rootPath = environment.getExecutionStepInfo().getPath().toString();
    String[] rootPaths = StringUtil.tokenizeToStringArray(rootPath, "/");
    Field rootField =
        ObjectUtil.find(
            fields,
            item -> rootPaths[0].equals(item.getAlias()) || rootPaths[0].equals(item.getName()));
    for (String path : paths) {
      String[] internalPaths = StringUtil.tokenizeToStringArray(path, ".");
      assert rootField != null;
      List<Field> selections = rootField.getSelectionSet().getSelectionsOfType(Field.class);
      for (String fieldName : internalPaths) {
        Field node = ObjectUtil.find(selections, item -> fieldName.equals(item.getName()));
        if (node == null) {
          return false;
        }
        selections = node.getSelectionSet().getSelectionsOfType(Field.class);
      }
    }
    return true;
  }
}
