package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.InputStream;
import java.io.Reader;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.spring.config.JacksonConfig;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Slf4j
public class XML {

  private static final ObjectMapperWrapper wrapper = new ObjectMapperWrapper();

  public static synchronized void setObjectMapper(ObjectMapper objectMapper) {
    wrapper.setObjectMapper(objectMapper);
  }

  public static synchronized void initialize() {
    Jackson2ObjectMapperBuilder xmlMapperBuilder = Jackson2ObjectMapperBuilder.xml();
    new JacksonConfig.AnyJackson2XmlMapperBuilderCustomizer().customize(xmlMapperBuilder);
    wrapper.setObjectMapper(xmlMapperBuilder.build());
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
    if (wrapper.getObjectMapper() == null) {
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
