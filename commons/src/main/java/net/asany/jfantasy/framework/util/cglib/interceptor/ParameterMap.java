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
package net.asany.jfantasy.framework.util.cglib.interceptor;

import java.util.*;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;

public class ParameterMap extends HashMap<String, Object> {

  @Override
  public boolean containsKey(Object key) {
    return super.containsKey(key);
  }

  @Override
  public Object put(String key, Object value) {
    if (value instanceof String) {
      if (isArray(key)) {
        String propertyName = getPropertyName(key);
        String name = RegexpUtil.replaceFirst(key, "\\[.*?\\]", "");
        if (!super.containsKey(propertyName)) {
          super.put(propertyName, new ArrayList<Object>());
        }
        String seq = RegexpUtil.parseGroup(key, "^" + propertyName + "\\[(\\d+)\\]", 1);
        if (isBean(name)) {
          List<Object> array = (List<Object>) super.get(propertyName);
          int index = Integer.parseInt(seq);
          if (array.size() <= index) {
            for (int i = array.size(); i <= index; i++) {
              array.add(array.get(i));
            }
          }
          if (array.get(index) == null) {
            array.set(index, new ParameterMap());
          }
          ((ParameterMap) array.get(index))
              .put(RegexpUtil.replace(name, "^" + propertyName + ".", ""), value);
        } else {
          List<Object> array = (List<Object>) super.get(propertyName);
          int index = Integer.parseInt(seq);
          if (array.size() <= index) {
            for (int i = array.size(); i <= index; i++) {
              array.add(array.get(i));
            }
          }
          array.set(index, value);
        }
      } else if (isBean(key)) {
        String propertyName = getPropertyName(key);
        if (!super.containsKey(propertyName)) {
          super.put(propertyName, new ParameterMap());
        }
        ((ParameterMap) super.get(propertyName))
            .put(RegexpUtil.replace(key, "^" + propertyName + ".", ""), value);
      }
    }
    return super.put(key, value);
  }

  private static String getPropertyName(String param) {
    String returnVal = param;
    if (isBean(param)) {
      String[] fieldNames = param.split("\\.");
      return getPropertyName(fieldNames[0]);
    }
    String regEx = "(.*?)\\[\\d\\]$";
    String group = RegexpUtil.parseGroup(param, regEx, 1);
    if (group != null) {
      returnVal = group;
    }
    return returnVal;
  }

  private static boolean isBean(String fieldName) {
    if (!fieldName.contains(".") && !RegexpUtil.find(fieldName, "^[0-9]+$")) {
      return false;
    }
    return true;
  }

  private static boolean isArray(String fieldName) {
    if (isBean(fieldName)) {
      fieldName = fieldName.split("\\.")[0];
    }
    String regEx = "\\[\\d+\\]$";
    return RegexpUtil.isMatch(fieldName, regEx);
  }
}
