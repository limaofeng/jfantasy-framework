package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.spring.ClassPathScanner;
import org.jfantasy.framework.util.asm.AnnotationDescriptor;
import org.jfantasy.framework.util.asm.AsmUtil;
import org.jfantasy.framework.util.common.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 在当前线程内保存ObjectMapper供Jackson2HttpMessageConverter使用
 */
public class MixInHolder {

    private static final Map<Class<?>, MixInSource> mixInSourceMap = new HashMap<>();

    public static MixInSource createMixInSource(Class<?> target) {
        if (!mixInSourceMap.containsKey(target)) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            Class mixIn = AsmUtil.makeInterface("org.jfantasy.framework.jackson.mixin." + target.getSimpleName() + "_" + uuid, AnnotationDescriptor.builder(JsonFilter.class).setValue("value", uuid).build());
            MixInSource mixInSource = new MixInSource(uuid, target, mixIn);
            mixInSourceMap.put(target, mixInSource);
            return mixInSource;
        }
        return mixInSourceMap.get(target);
    }

    public static Map<Class<?>, Class<?>> getSourceMixins() {
        Map<Class<?>, Class<?>> sourceMixins = new HashMap<>();
        for (MixInSource mixInSource : mixInSourceMap.values()) {
            sourceMixins.put(mixInSource.getTarget(), mixInSource.getMixIn());
        }
        return sourceMixins;
    }

    public static void scan(Class<?>... classes) {
        for (Class clazz : classes) {
            createMixInSource(clazz);
        }
    }

    public static void scan(String... basePackages) {
        for (String basePackage : basePackages) {
            if (StringUtil.isBlank(basePackage)) {
                continue;
            }
            scan(ClassPathScanner.getInstance().findAnnotationedClasses(basePackage, JsonIgnoreProperties.class).toArray(new Class[0]));
        }
    }

    public static class MixInSource {
        private String filterName;
        private Class<?> target;
        private Class<?> mixIn;

        MixInSource(String filterName, Class<?> target, Class<?> mixIn) {
            this.filterName = filterName;
            this.target = target;
            this.mixIn = mixIn;
        }

        public String getFilterName() {
            return filterName;
        }

        public String getId() {
            return filterName;
        }

        public Class<?> getTarget() {
            return target;
        }

        Class<?> getMixIn() {
            return mixIn;
        }
    }

}