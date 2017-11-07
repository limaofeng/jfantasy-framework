package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanPropertyFilter extends SimpleBeanPropertyFilter {

    private Set<Class<?>> types = new HashSet<>();

    private Map<Class<?>, Set<String>> includeMap = new ConcurrentHashMap<>();

    private Map<Class<?>, Set<String>> excludeMap = new ConcurrentHashMap<>();

    public Set<Class<?>> getTypes() {
        return types;
    }

    /**
     * 只返回的字段
     *
     * @param type
     * @param fields
     */
    public BeanPropertyFilter includes(Class<?> type, String... fields) {
        return addToMap(includeMap, type, fields);
    }

    /**
     * 排除字段
     *
     * @param type
     * @param fields
     */
    public BeanPropertyFilter excludes(Class<?> type, String... fields) {
        return addToMap(excludeMap, type, fields);
    }

    private BeanPropertyFilter addToMap(Map<Class<?>, Set<String>> map, Class<?> type, String[] fields) {
        if (fields.length == 0) {
            return this;
        }
        types.add(type);
        Set<String> fieldSet = map.getOrDefault(type, new HashSet<>());
        fieldSet.addAll(Arrays.asList(fields));
        map.put(type, fieldSet);
        return this;
    }

    private boolean apply(Class<?> type, String name) {
        Set<String> includeFields = includeMap.get(type);
        Set<String> excludeFields = excludeMap.get(type);
        if (includeFields != null && includeFields.contains(name)) {
            return true;
        } else if (excludeFields != null && !excludeFields.contains(name)) {
            return true;
        } else if (includeFields == null && excludeFields == null) {
            return true;
        }
        return false;
    }

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider prov, PropertyWriter writer)
            throws Exception {
        if (apply(pojo.getClass(), writer.getName())) {
            writer.serializeAsField(pojo, jgen, prov);
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, prov);
        }
    }

    public Builder mixin(Class<?> type) {
        return new Builder(this, type);
    }

    public static Builder newBuilder(Class type) {
        return new Builder(type);
    }

    public static class Builder {
        private BeanPropertyFilter filter;
        private Class<?> current;

        public Builder(BeanPropertyFilter filter, Class type) {
            this.filter = filter;
            this.current = JSON.mixin(type);
        }

        public Builder(Class type) {
            this(new BeanPropertyFilter(), type);
        }

        public BeanPropertyFilter build() {
            return filter;
        }

        public Builder type(Class<?> type) {
            this.current = JSON.mixin(type);
            return this;
        }

        public Builder excludes(String... names) {
            if (names.length == 0) {
                return this;
            }
            filter.excludes(this.current, names);
            return this;
        }

        public Builder includes(String... names) {
            if (names.length == 0) {
                return this;
            }
            filter.includes(this.current, names);
            return this;
        }
    }


}