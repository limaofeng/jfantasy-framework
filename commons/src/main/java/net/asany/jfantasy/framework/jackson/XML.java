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
package net.asany.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.InputStream;
import java.io.Reader;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Slf4j
public class XML {

  private static ObjectMapperWrapper wrapper;

  public static synchronized ObjectMapperWrapper initialize() {
    Jackson2ObjectMapperBuilder xmlMapperBuilder = Jackson2ObjectMapperBuilder.xml();
    ObjectMapper objectMapper = xmlMapperBuilder.build();
    return initialize(objectMapper);
  }

  public static synchronized ObjectMapperWrapper initialize(
      Function<Jackson2ObjectMapperBuilder, Jackson2ObjectMapperBuilder> customizer) {
    Jackson2ObjectMapperBuilder xmlMapperBuilder = Jackson2ObjectMapperBuilder.xml();
    ObjectMapper objectMapper = customizer.apply(xmlMapperBuilder).build();
    return initialize(objectMapper);
  }

  public static synchronized ObjectMapperWrapper initialize(ObjectMapper objectMapper) {
    if (wrapper != null) {
      log.warn("重置 XML 工具类中的 ObjectMapper 对象.");
      wrapper.setObjectMapper(objectMapper);
      return wrapper;
    }
    return wrapper = new ObjectMapperWrapper(objectMapper);
  }

  public static String serialize(Object object, String... ignoreProperties) {
    return wrapper.serialize(object, ignoreProperties);
  }

  public static String serialize(Object object, ObjectFilter filter) {
    return wrapper.serialize(object, filter);
  }

  public static String serialize(Object object, FilterProvider provider) {
    return wrapper.serialize(object, provider);
  }

  public static <T> T deserialize(InputStream input, Class<T> classed) {
    return wrapper.deserialize(input, classed);
  }

  public static <T> T deserialize(Reader src, Class<T> classed) {
    return wrapper.deserialize(src, classed);
  }

  public static <T> T deserialize(String json, Class<T> classed) {
    return wrapper.deserialize(json, classed);
  }

  public static <T> T[] deserialize(String json, T[] classed) {
    return wrapper.deserialize(json, classed);
  }

  public static <T> T deserialize(String json, TypeReference<T> typeReference) {
    return wrapper.deserialize(json, typeReference);
  }

  private static boolean mixinLogWarning = true;

  public static void mixin(Class<?> type) {
    if (wrapper == null) {
      if (mixinLogWarning) {
        log.warn("XmlMapper 未初始化完成,无法进行混入操作");
        mixinLogWarning = false;
      }
      return;
    }
    ObjectMapper objectMapper = wrapper.getObjectMapper();
    if (objectMapper.findMixInClassFor(type) == null) {
      FilteredMixinHolder.MixInSource mixInSource = FilteredMixinHolder.createMixInSource(type);
      objectMapper.addMixIn(mixInSource.getType(), mixInSource.getMixIn());
    }
  }
}
