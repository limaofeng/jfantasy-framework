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
package net.asany.jfantasy.framework.util.ognl.typeConverter;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Date;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.DateUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import ognl.DefaultTypeConverter;
import ognl.OgnlContext;

public class DateConverter extends DefaultTypeConverter {

  @Override
  public Object convertValue(
      OgnlContext context,
      Object target,
      Member member,
      String propertyName,
      Object value,
      Class toType) {
    if (Date.class.isAssignableFrom(toType)) {
      String dateFormatString;
      DateFormat dateFormat = ClassUtil.getParamAnno((Method) member, DateFormat.class);
      dateFormatString = dateFormat != null ? dateFormat.pattern() : "yyyy-MM-dd HH:mm:ss";
      return DateUtil.parse(
          StringUtil.nullValue(ClassUtil.isArray(value) ? Array.get(value, 0) : value),
          dateFormatString);
    }
    return super.convertValue(context, target, member, propertyName, value, toType);
  }
}
