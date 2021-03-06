package org.jfantasy.framework.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jfantasy.framework.util.asm.AnnotationDescriptor;
import org.jfantasy.framework.util.asm.AsmUtil;

/**
 * @author limaofeng
 * @version V1.0
 * @date 07/11/2017 8:38 PM
 */
public class MixInHolder {
  private static final SimpleFilterProvider defaultFilterProvider = new SimpleFilterProvider();
  private static final Map<Class<?>, MixInSource> mixInSourceMap = new ConcurrentHashMap<>();

  public static MixInSource createMixInSource(Class<?> type) {
    if (!mixInSourceMap.containsKey(type)) {
      String uuid = UUID.randomUUID().toString().replaceAll("-", "");
      Class mixIn =
          AsmUtil.makeInterface(
              "org.jfantasy.framework.jackson.mixin." + type.getSimpleName() + "_" + uuid,
              AnnotationDescriptor.builder(JsonFilter.class).setValue("value", uuid).build());
      MixInSource mixInSource = new MixInSource(uuid, type, mixIn);
      mixInSourceMap.putIfAbsent(type, mixInSource);
      defaultFilterProvider.addFilter(
          mixInSource.getId(), BeanPropertyFilter.newBuilder(type).build());
      return mixInSource;
    }
    return mixInSourceMap.get(type);
  }

  public static FilterProvider getDefaultFilterProvider() {
    return defaultFilterProvider;
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
