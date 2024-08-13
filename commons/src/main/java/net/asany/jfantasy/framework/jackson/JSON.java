package net.asany.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
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

  private static final ObjectMapperWrapper wrapper = ObjectMapperWrapper.DEFAULT;

  public static synchronized ObjectMapperWrapper initialize() {
    Jackson2ObjectMapperBuilder jsonMapperBuilder = new Jackson2ObjectMapperBuilder();
    ObjectMapper objectMapper = jsonMapperBuilder.build();
    return initialize(objectMapper);
  }

  public static synchronized ObjectMapperWrapper initialize(ObjectMapper objectMapper) {
    if (wrapper.getObjectMapper() != null) {
      log.warn("重置 JSON 工具类中的 ObjectMapper 对象.");
    }
    wrapper.setObjectMapper(objectMapper);
    return wrapper;
  }

  public static synchronized ObjectMapperWrapper initialize(
      Function<Jackson2ObjectMapperBuilder, Jackson2ObjectMapperBuilder> customizer) {
    Jackson2ObjectMapperBuilder jsonMapperBuilder = new Jackson2ObjectMapperBuilder();
    ObjectMapper objectMapper = customizer.apply(jsonMapperBuilder).build();
    return initialize(objectMapper);
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

  public static <L extends Collection<T>, T> L deserialize(
      String json, Class<L> listClass, Class<T> classed) {
    TypeFactory typeFactory = getObjectMapper().getTypeFactory();
    Type type = typeFactory.constructCollectionType(listClass, classed);
    return wrapper.deserialize(json, typeFactory.constructType(type));
  }

  public static JsonNode deserialize(String json) {
    try {
      return wrapper.getObjectMapper().readTree(json);
    } catch (IOException e) {
      log.error("{} source json string : {} => readNode", e.getMessage(), json, e);
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
