package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.deserializer.DateDeserializer;
import org.jfantasy.framework.jackson.serializer.DateSerializer;
import org.jfantasy.framework.util.common.ClassUtil;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class JSON {

    private static final Log LOG = LogFactory.getLog(JSON.class);

    private static ObjectMapper objectMapper;
    private static XmlMapper xmlMapper;
    private static XmlUtil xmlUtil;

    static {
        initialize(new ObjectMapper());
    }

    private static synchronized void initialize(ObjectMapper objectMapper) {
        if (JSON.objectMapper != null) {
            LOG.warn("重置 JSON 工具类中的 ObjectMapper 对象.");
        }
        JSON.objectMapper = objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(new SimpleModule()
                .addSerializer(Date.class, new DateSerializer("yyyy-MM-dd HH:mm:ss"))
                .addDeserializer(Date.class, new DateDeserializer()));

        JacksonXmlModule xmlModule = new JacksonXmlModule();
        JSON.xmlMapper = new XmlMapper(xmlModule);
        xmlMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        xmlUtil = new XmlUtil(JSON.xmlMapper);
    }

    public static String serialize(Object object, String... ignoreProperties) {
        if (object == null) {
            return null;
        }
        return serialize(object, (builder) -> builder.excludes(ignoreProperties));
    }

    public static String serialize(Object object, Filter filter) {
        if (object == null) {
            return null;
        }
        Class type = ClassUtil.getRealType(object.getClass());
        if (type.isArray()) {
            type = type.getComponentType();
        } else if (List.class.isAssignableFrom(type)) {
            if (((List) object).size() == 0) {
                type = null;
            } else {
                type = ClassUtil.getRealType(((List) object).get(0).getClass());
            }
        }
        try {
            if (type != null) {
                SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
                provider.setDefaultFilter(filter.setup(BeanPropertyFilter.newBuilder(type)).build());
                return objectMapper.writer(provider).writeValueAsString(object);
            }
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return "";
    }

    public static String serialize(Object object, FilterProvider provider) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writer(provider).writeValueAsString(object);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return "";
    }

    public static JsonNode deserialize(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            LOG.error(e.getMessage() + " source json string : " + json + " => readNode", e);
        }
        return null;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    public static <T> T deserialize(String json, Class<T> classed) {
        try {
            return objectMapper.readValue(json, classed);
        } catch (IOException e) {
            LOG.error(e.getMessage() + " source json string : " + json + " => " + classed, e);
        }
        return null;
    }

    public static <T> T[] deserialize(String json, T[] classed) {
        try {
            return (T[]) objectMapper.readValue(json, classed.getClass());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T deserialize(String json, TypeReference<T> typeReference) {
        try {
            return (T) objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
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
        private XmlMapper xmlMapper;

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