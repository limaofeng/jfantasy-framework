package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.jfantasy.framework.util.asm.AnnotationDescriptor;
import org.jfantasy.framework.util.asm.AsmUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 07/11/2017 8:38 PM
 */
public class MixInHolder {

    private static final Map<Class<?>, MixInSource> mixInSourceMap = new ConcurrentHashMap<>();

    public static MixInSource createMixInSource(Class<?> type) {
        if (!mixInSourceMap.containsKey(type)) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            Class mixIn = AsmUtil.makeInterface("org.jfantasy.framework.jackson.mixin." + type.getSimpleName() + "_" + uuid, AnnotationDescriptor.builder(JsonFilter.class).setValue("value", uuid).build());
            MixInSource mixInSource = new MixInSource(uuid, type, mixIn);
            mixInSourceMap.putIfAbsent(type, mixInSource);
            return mixInSource;
        }
        return mixInSourceMap.get(type);
    }

    public static Map<Class<?>, Class<?>> getSourceMixins() {
        Map<Class<?>, Class<?>> sourceMixins = new HashMap<>();
        for (MixInSource mixInSource : mixInSourceMap.values()) {
            sourceMixins.put(mixInSource.getType(), mixInSource.getMixIn());
        }
        return sourceMixins;
    }

    public static class MixInSource {
        private String id;
        private Class<?> type;
        private Class<?> mixIn;

        MixInSource(String id, Class<?> type, Class<?> mixIn) {
            this.id = id;
            this.type = type;
            this.mixIn = mixIn;
        }

        public String getId() {
            return id;
        }

        public Class<?> getType() {
            return type;
        }

        Class<?> getMixIn() {
            return mixIn;
        }
    }

}
