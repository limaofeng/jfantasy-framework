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
package net.asany.jfantasy.framework.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.IgnoreException;
import net.asany.jfantasy.framework.util.common.BeanUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

@Slf4j
public class DateDeserializer extends JsonDeserializer<Date> {

  @Override
  public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonToken t = jp.getCurrentToken();
    if (t == JsonToken.VALUE_NUMBER_INT) {
      return new Date(jp.getNumberValue().longValue());
    } else if (t == JsonToken.VALUE_STRING) {
      String value = jp.getText().trim();
      if (StringUtil.isBlank(value)) {
        return null;
      }
      try {
        return BeanUtil.convertStringToObject(value, Date.class);
      } catch (Exception e) {
        log.debug("不能转换日期格式[{}]", value, e);
        throw new IgnoreException(e.getMessage(), e);
      }
    }
    throw new IgnoreException("JsonToken = " + t + ",是不能处理的类型！");
  }
}
