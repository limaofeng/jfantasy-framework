package net.asany.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * JSON 工具类
 *
 * @author limaofeng
 */
@Slf4j
public class JSON {

  private static final ObjectMapperWrapper wrapper = new ObjectMapperWrapper();

  public static synchronized ObjectMapperWrapper initialize() {
    if (wrapper.getObjectMapper() != null) {
      log.warn("重置 JSON 工具类中的 ObjectMapper 对象.");
    }
    Jackson2ObjectMapperBuilder jsonMapperBuilder = new Jackson2ObjectMapperBuilder();
    wrapper.setObjectMapper(jsonMapperBuilder.build());
    return wrapper;
  }

  public static synchronized ObjectMapperWrapper initialize(
      Function<Jackson2ObjectMapperBuilder, Jackson2ObjectMapperBuilder> customizer) {
    if (wrapper.getObjectMapper() != null) {
      log.warn("重置 JSON 工具类中的 ObjectMapper 对象.");
    }
    Jackson2ObjectMapperBuilder jsonMapperBuilder = new Jackson2ObjectMapperBuilder();
    wrapper.setObjectMapper(customizer.apply(jsonMapperBuilder).build());
    return wrapper;
  }

  public static synchronized void setObjectMapper(ObjectMapper objectMapper) {
    wrapper.setObjectMapper(objectMapper);
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

  public static String stringify(Object object) {
    return serialize(object);
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

  public static JsonNode deserialize(String json) {
    try {
      return wrapper.getObjectMapper().readTree(json);
    } catch (IOException e) {
      log.error(e.getMessage() + " source json string : " + json + " => readNode", e);
    }
    return null;
  }

  private static boolean mixinLogWarning = true;

  public static void mixin(Class<?> type) {
    if (wrapper.getObjectMapper() == null) {
      if (mixinLogWarning) {
        log.warn("JsonMapper 未初始化完成,无法进行混入操作");
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

  public static ObjectMapper getObjectMapper() {
    return wrapper.getObjectMapper();
  }

  public static JsonNode findNode(JsonNode rootNode, String path) {
    JsonNode foundNode = rootNode.at(path);
    if (foundNode.isMissingNode()) {
      return null;
    }
    return foundNode;
  }
}
