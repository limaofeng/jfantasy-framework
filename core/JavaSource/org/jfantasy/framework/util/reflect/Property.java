package org.jfantasy.framework.util.reflect;

import org.jfantasy.framework.util.common.ClassUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class Property {
    private String name;
    private MethodProxy readMethodProxy;
    private MethodProxy writeMethodProxy;
    private Class propertyType;
    private boolean write;
    private boolean read;
    private Map<Class<Annotation>, Annotation> annotationCache = new HashMap<>();

    Property(String name, MethodProxy readMethodProxy, MethodProxy writeMethodProxy, Class<?> propertyType) {
        this.read = readMethodProxy != null;
        this.write = writeMethodProxy != null;
        this.name = name;
        this.readMethodProxy = readMethodProxy;
        this.writeMethodProxy = writeMethodProxy;
        this.propertyType = propertyType;
    }

    public boolean isWrite() {
        return this.write;
    }

    public boolean isRead() {
        return this.read;
    }

    public Object getValue(Object target) {
        if (!this.read) {
            return null;
        }
        return this.readMethodProxy.invoke(target);
    }

    public void setValue(Object target, Object value) {
        if (!this.write) {
            return;
        }
        this.writeMethodProxy.invoke(target, value);
    }

    public <T> Class<T> getPropertyType() {
        return this.propertyType;
    }

    public String getName() {
        return this.name;
    }

    public Annotation getAnnotation(Class<Annotation> tClass) {
        if (annotationCache.containsKey(tClass)) {
            return annotationCache.get(tClass);
        }
        Annotation annotation = null;
        Class<?> declaringClass = null;
        if (this.isRead()) {
            annotation = this.getReadMethod().getAnnotation(tClass);
            if (annotation == null) {
                declaringClass = this.getReadMethod().getDeclaringClass();
            }
        }
        if (annotation == null && this.isWrite()) {
            annotation = this.getWriteMethod().getAnnotation(tClass);
            if (annotation == null) {
                declaringClass = this.getWriteMethod().getDeclaringClass();
            }
        }
        if (annotation == null) {
            Field field = ClassUtil.getDeclaredField(declaringClass, this.name);
            if (field != null) {
                annotation = field.getAnnotation(tClass);
            }
        }
        annotationCache.put(tClass, annotation);
        return annotation;
    }

    public MethodProxy getReadMethod() {
        return this.readMethodProxy;
    }

    public MethodProxy getWriteMethod() {
        return this.writeMethodProxy;
    }

    public ParameterizedType getGenericType() {
        return (ParameterizedType) this.getReadMethod().getMethod().getGenericReturnType();
    }
}
