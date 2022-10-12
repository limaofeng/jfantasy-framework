package org.jfantasy.framework.dao.hibernate.converter;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DESConverterTest {

  private final DESConverter converter = new DESConverter("hooluesoft");

  @Test
  public void testConvertToDatabaseColumn() {
    log.debug(converter.convertToDatabaseColumn("中文"));

    log.debug(converter.convertToDatabaseColumn("李茂峰"));

    log.debug(converter.convertToDatabaseColumn("15921884771"));

    log.debug(
        new DESConverter()
            .convertToDatabaseColumn(new String(new byte[] {63, 63, 63}, StandardCharsets.UTF_8)));
    log.debug(new DESConverter().convertToDatabaseColumn("李茂峰"));
  }

  @Test
  public void testConvertToEntityAttribute() {
    log.debug(converter.convertToEntityAttribute("d699ef0e932711e6"));

    log.debug(converter.convertToEntityAttribute("11196231db5fc99eff08925a14c3dae5"));

    log.debug(converter.convertToEntityAttribute("4ae53296a74f57280e2c7067d5a53bba"));

    log.debug(new DESConverter().convertToEntityAttribute("fefd3fca92327867"));
  }
}
