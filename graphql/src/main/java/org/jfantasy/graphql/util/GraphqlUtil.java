package org.jfantasy.graphql.util;

import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * Graphql 工具类
 *
 * @author limaofeng
 */
public class GraphqlUtil {

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
