package net.asany.jfantasy.framework.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;

public class ColorsSerializer {

  private ThreadLocal<ObjectMapper> mapper = ThreadLocal.withInitial(ObjectMapper::new);

  private FilteredMixinFilter propertyFilter = new FilteredMixinFilter();

  public void filter(Class<?> clazz, String include, String filter) {
    if (clazz == null) {
      return;
    }
    if (StringUtils.isNotBlank(include)) {
      propertyFilter.includes(clazz, include.split(","));
    }
    if (StringUtils.isNotBlank(filter)) {
      propertyFilter.excludes(clazz, filter.split(","));
    }
    mapper.get().addMixIn(clazz, FilteredMixinFilter.class);
  }

  public String toJson(Object object) throws JsonProcessingException {
    SimpleFilterProvider provider = new SimpleFilterProvider();
    provider.setDefaultFilter(propertyFilter);
    return mapper.get().setFilterProvider(provider).writeValueAsString(object);
  }

  public void filter(JsonResultFilter jsonResultFilter) {
    this.filter(jsonResultFilter.type(), jsonResultFilter.include(), jsonResultFilter.filter());
  }
}
