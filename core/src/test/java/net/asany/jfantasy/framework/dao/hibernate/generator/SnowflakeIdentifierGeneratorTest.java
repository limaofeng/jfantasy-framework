package net.asany.jfantasy.framework.dao.hibernate.generator;

import java.io.Serializable;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class SnowflakeIdentifierGeneratorTest {

  private final SnowflakeIdentifierGenerator snowflakeGenerator =
      new SnowflakeIdentifierGenerator();

  @BeforeEach
  public void before() {
    snowflakeGenerator.configure(null, new Properties(), null);
  }

  @Test
  void generate() {
    Serializable id = snowflakeGenerator.generate(null, null);
    log.info("id: {}", id);
  }
}
