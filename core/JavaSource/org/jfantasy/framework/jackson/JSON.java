package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.deserializer.DateDeserializer;
import org.jfantasy.framework.jackson.serializer.DateSerializer;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JSON {

    private static final Log LOG = LogFactory.getLog(JSON.class);

    private static ObjectMapper objectMapper;

    public static synchronized ObjectMapper initialize(ObjectMapper objectMapper) {
        if (JSON.objectMapper != null) {
            LOG.warn("重置 JSON 工具类中的 ObjectMapper 对象.");
        }
        JSON.objectMapper = objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)//为空的字段不序列化
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)// 当找不到对应的序列化器时 忽略此字段
                .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)// 允许非空字段
                .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)// 允许单引号
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
                .registerModule(new SimpleModule()// 默认日期转换方式
                        .addSerializer(Date.class, new DateSerializer("yyyy-MM-dd HH:mm:ss"))
                        .addDeserializer(Date.class, new DateDeserializer()));
        return objectMapper;
    }

    public static String serialize(Object object, String... ignoreProperties) {
        if (object == null) {
            return null;
        }
        return serialize(object, () -> {
            Map<Class, String[]> data = new HashMap<>();
            data.put(object.getClass(), ignoreProperties);
            return data;
        });
    }

    private static String serialize(Object object, IgnoreProperties ignoreProperties) {
        if (object == null) {
            return null;
        }
        try {
            SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
            for (Map.Entry<Class, String[]> entry : ignoreProperties.fields().entrySet()) {
                ThreadJacksonMixInHolder.MixInSource mixInSource = ThreadJacksonMixInHolder.createMixInSource(entry.getKey());
                provider.addFilter(mixInSource.getFilterName(), SimpleBeanPropertyFilter.serializeAllExcept(entry.getValue()));
            }
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

    @SuppressWarnings("unchecked")
    public static <T> T[] deserialize(String json, T[] classed) {
        try {
            return (T[]) objectMapper.readValue(json, classed.getClass());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String json, TypeReference<T> typeReference) {
        try {
            return (T) objectMapper.readValue(json, typeReference);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    interface IgnoreProperties {

        Map<Class, String[]> fields();

    }

}