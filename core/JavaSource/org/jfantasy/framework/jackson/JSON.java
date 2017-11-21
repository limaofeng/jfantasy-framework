package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.deserializer.DateDeserializer;
import org.jfantasy.framework.jackson.serializer.DateSerializer;
import org.jfantasy.framework.util.common.ClassUtil;

import java.io.IOException;
import java.util.Date;

public class JSON {

    private static final Log LOG = LogFactory.getLog(JSON.class);

    private static ObjectMapper objectMapper = initialize(new ObjectMapper());

    public static synchronized ObjectMapper initialize(ObjectMapper objectMapper) {
        if (JSON.objectMapper != null) {
            LOG.warn("重置 JSON 工具类中的 ObjectMapper 对象.");
        }
        JSON.objectMapper = objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                //为空的字段不序列化
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                // 当找不到对应的序列化器时 忽略此字段
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                // 允许非空字段
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
                // 允许单引号
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                // 转义字符异常
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS)
                // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                // 默认日期转换方式
                .registerModule(new SimpleModule()
                        .addSerializer(Date.class, new DateSerializer("yyyy-MM-dd HH:mm:ss"))
                        .addDeserializer(Date.class, new DateDeserializer()));
        return objectMapper;
    }

    public static String serialize(Object object, String... ignoreProperties) {
        if (object == null) {
            return null;
        }
        if (ignoreProperties.length != 0 && ClassUtil.isBeanType(object.getClass())) {
            serialize(object, (builder) -> builder.excludes(ignoreProperties));
        }
        try {
            return objectMapper.writer().writeValueAsString(object);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public static String serialize(Object object, Filter filter) {
        if (object == null) {
            return null;
        }
        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
        provider.setDefaultFilter(filter.setup(BeanPropertyFilter.newBuilder(ClassUtil.getRealType(object.getClass()))).build());
        return serialize(object, provider);
    }

    public static String serialize(Object object, FilterProvider provider) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writer(provider).writeValueAsString(object);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
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
        return objectMapper != null ? objectMapper : initialize(new ObjectMapper());
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

    public interface Filter {

        BeanPropertyFilter.Builder setup(BeanPropertyFilter.Builder builder);
    }

}