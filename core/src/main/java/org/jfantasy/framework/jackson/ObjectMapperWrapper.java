package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.ClassUtil;

@Getter
@Slf4j
public class ObjectMapperWrapper {

  private ObjectMapper objectMapper;

  //  private JSON.XmlUtil xmlUtil;

  //  public synchronized void initialize() {
  //    if (jsonMapper != null && xmlMapper != null) {
  //      Jackson2ObjectMapperBuilder jsonMapperBuilder = new Jackson2ObjectMapperBuilder();
  //      new JacksonConfig.AnyJackson2ObjectMapperBuilderCustomizer().customize(jsonMapperBuilder);
  //      setJsonMapper(jsonMapperBuilder.build());
  //
  //      Jackson2ObjectMapperBuilder xmlMapperBuilder = Jackson2ObjectMapperBuilder.xml();
  //      new JacksonConfig.AnyJackson2XmlMapperBuilderCustomizer().customize(xmlMapperBuilder);
  //      setXmlMapper(xmlMapperBuilder.build());
  //    }
  //  }

  //  public synchronized void setXmlMapper(XmlMapper xmlMapper) {
  //    if (JSON.xmlMapper != null) {
  //      log.warn("重置 JSON 工具类中的 XmlMapper 对象.");
  //    }
  //    JSON.xmlMapper = xmlMapper;
  //    xmlUtil = new JSON.XmlUtil((XmlMapper) JSON.xmlMapper);
  //  }

  @SneakyThrows
  public String serialize(Object object, String... ignoreProperties) {
    if (object == null) {
      return null;
    }
    if (ignoreProperties.length > 0) {
      return serialize(object, (builder) -> builder.excludes(ignoreProperties));
    }
    return this.objectMapper.writeValueAsString(object);
  }

  @SneakyThrows
  public String serialize(Object object, ObjectFilter filter) {
    if (object == null) {
      return null;
    }
    Class<?> type = ClassUtil.getRealType(object.getClass());
    if (type.isArray()) {
      type = type.getComponentType();
    }
    if (List.class.isAssignableFrom(type)) {
      if (((List<?>) object).isEmpty()) {
        type = null;
      } else {
        type = ClassUtil.getRealType(((List<?>) object).get(0).getClass());
      }
    }
    if (type == null || ClassUtil.isPrimitiveOrWrapper(type) || ClassUtil.isMap(type)) {
      return this.objectMapper.writeValueAsString(object);
    }
    SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
    provider.setDefaultFilter(filter.setup(FilteredMixinFilter.newBuilder(type)).build());
    return this.objectMapper.writer(provider).writeValueAsString(object);
  }

  @SneakyThrows
  public String serialize(Object object, FilterProvider provider) {
    if (object == null) {
      return null;
    }
    return this.objectMapper.writer(provider).writeValueAsString(object);
  }

  public JsonNode deserialize(String json) {
    try {
      return this.objectMapper.readTree(json);
    } catch (IOException e) {
      log.error(e.getMessage() + " source json string : " + json + " => readNode", e);
    }
    return null;
  }

  public <T> T deserialize(InputStream input, Class<T> classed) {
    try {
      return this.objectMapper.readValue(input, classed);
    } catch (IOException e) {
      log.error(e.getMessage() + " source input stream => " + classed, e);
    }
    return null;
  }

  public <T> T deserialize(Reader src, Class<T> classed) {
    try {
      return this.objectMapper.readValue(src, classed);
    } catch (IOException e) {
      log.error(e.getMessage() + " source reader => " + classed, e);
    }
    return null;
  }

  public <T> T deserialize(String json, Class<T> classed) {
    try {
      return this.objectMapper.readValue(json, classed);
    } catch (IOException e) {
      log.error(e.getMessage() + " source json string : " + json + " => " + classed, e);
    }
    return null;
  }

  public <T> T[] deserialize(String json, T[] classed) {
    try {
      return (T[]) this.objectMapper.readValue(json, classed.getClass());
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public <T> T deserialize(String json, TypeReference<T> typeReference) {
    try {
      return this.objectMapper.readValue(json, typeReference);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }

  public ObjectMapper getObjectMapper() {
    return this.objectMapper;
  }

  public void setObjectMapper(ObjectMapper objectMapper) {
    objectMapper.setMixInResolver(FilteredMixinHolder.getMixInResolver());
    this.objectMapper = objectMapper;
  }
}
