package net.asany.jfantasy.framework.jackson;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public interface Jackson2XmlMapperBuilderCustomizer {

  void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder);
}
