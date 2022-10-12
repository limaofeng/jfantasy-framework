package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.deserializer.DateDeserializer;
import org.jfantasy.framework.jackson.serializer.DateSerializer;
import org.jfantasy.framework.util.common.ClassUtil;

/**
 * JSON 工具类
 *
 * @author limaofeng
 */
@Slf4j
public class JSON {

  private static ObjectMapper objectMapper;
  private static XmlMapper xmlMapper;
  private static XmlUtil xmlUtil;

  static {
    initialize(new ObjectMapper());
  }

  public static synchronized void initialize(ObjectMapper objectMapper) {
    if (JSON.objectMapper != null) {
      log.warn("重置 JSON 工具类中的 ObjectMapper 对象.");
    }
    JSON.objectMapper =
        objectMapper
            .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(
                new SimpleModule()
                    .addSerializer(Date.class, new DateSerializer("yyyy-MM-dd HH:mm:ss"))
                    .addDeserializer(Date.class, new DateDeserializer()))
            .setFilterProvider(MixInHolder.getDefaultFilterProvider());

    JacksonXmlModule xmlModule = new JacksonXmlModule();
    JSON.xmlMapper = new XmlMapper(xmlModule);
    xmlMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    xmlUtil = new XmlUtil(JSON.xmlMapper);
  }

  @SneakyThrows
  public static String serialize(Object object, String... ignoreProperties) {
    if (object == null) {
      return null;
    }
    if (ignoreProperties.length > 0) {
      return serialize(object, (builder) -> builder.excludes(ignoreProperties));
    }
    return objectMapper.writeValueAsString(object);
  }

  @SneakyThrows
  public static String serialize(Object object, Filter filter) {
    if (object == null) {
      return null;
    }
    Class type = ClassUtil.getRealType(object.getClass());
    if (type.isArray()) {
      type = type.getComponentType();
    }
    if (List.class.isAssignableFrom(type)) {
      if (((List) object).isEmpty()) {
        type = null;
      } else {
        type = ClassUtil.getRealType(((List) object).get(0).getClass());
      }
    }
    if (type == null || ClassUtil.isPrimitiveOrWrapper(type) || ClassUtil.isMap(type)) {
      return objectMapper.writeValueAsString(object);
    }
    SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
    provider.setDefaultFilter(filter.setup(BeanPropertyFilter.newBuilder(type)).build());
    return objectMapper.writer(provider).writeValueAsString(object);
  }

  @SneakyThrows
  public static String serialize(Object object, FilterProvider provider) {
    if (object == null) {
      return null;
    }
    return objectMapper.writer(provider).writeValueAsString(object);
  }

  public static JsonNode deserialize(String json) {
    try {
      return objectMapper.readTree(json);
    } catch (IOException e) {
      log.error(e.getMessage() + " source json string : " + json + " => readNode", e);
    }
    return null;
  }

  public static ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  public static XmlMapper getXmlMapper() {
    return xmlMapper;
  }

  public static <T> T deserialize(InputStream input, Class<T> classed) {
    try {
      return objectMapper.readValue(input, classed);
    } catch (IOException e) {
      log.error(e.getMessage() + " source input stream => " + classed, e);
    }
    return null;
  }

  public static <T> T deserialize(Reader src, Class<T> classed) {
    try {
      return objectMapper.readValue(src, classed);
    } catch (IOException e) {
      log.error(e.getMessage() + " source reader => " + classed, e);
    }
    return null;
  }

  public static <T> T deserialize(String json, Class<T> classed) {
    try {
      return objectMapper.readValue(json, classed);
    } catch (IOException e) {
      log.error(e.getMessage() + " source json string : " + json + " => " + classed, e);
    }
    return null;
  }

  public static <T> T[] deserialize(String json, T[] classed) {
    try {
      return (T[]) objectMapper.readValue(json, classed.getClass());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public static <T> T deserialize(String json, TypeReference<T> typeReference) {
    try {
      return objectMapper.readValue(json, typeReference);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public static Class<?> mixin(Class<?> type) {
    if (objectMapper.findMixInClassFor(type) == null) {
      MixInHolder.MixInSource mixInSource = MixInHolder.createMixInSource(type);
      objectMapper.addMixIn(mixInSource.getType(), mixInSource.getMixIn());
    }
    return type;
  }

  public static XmlUtil xml() {
    return xmlUtil;
  }

  public interface Filter {

    BeanPropertyFilter.Builder setup(BeanPropertyFilter.Builder builder);
  }

  public static class XmlUtil {
    private final XmlMapper xmlMapper;

    public XmlUtil(XmlMapper xmlMapper) {
      this.xmlMapper = xmlMapper;
    }

    @SneakyThrows
    public String serialize(Object root, String rootName) {
      return this.xmlMapper.writer().withRootName(rootName).writeValueAsString(root);
    }

    @SneakyThrows
    public JsonNode deserialize(String xml) {
      return this.xmlMapper.readTree(xml);
    }

    @SneakyThrows
    public <T> T deserialize(String xml, Class<T> valueType) {
      return this.xmlMapper.readValue(xml, valueType);
    }
  }
}
