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

import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

public class GraphQLUtils {

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
