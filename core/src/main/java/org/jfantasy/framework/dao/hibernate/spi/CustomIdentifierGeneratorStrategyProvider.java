package org.jfantasy.framework.dao.hibernate.spi;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
import org.jfantasy.framework.dao.hibernate.generator.SequenceGenerator;
import org.jfantasy.framework.dao.hibernate.generator.SerialNumberGenerator;
import org.jfantasy.framework.dao.hibernate.generator.SnowflakeGenerator;

public class CustomIdentifierGeneratorStrategyProvider
    implements IdentifierGeneratorStrategyProvider {

  private final Map<String, Class<?>> strategies = new HashMap<>();

  {
    strategies.put("fantasy-sequence", SequenceGenerator.class);
    strategies.put("serial-number", SerialNumberGenerator.class);
    strategies.put("snowflake", SnowflakeGenerator.class);
  }

  @Override
  public Map<String, Class<?>> getStrategies() {
    return strategies;
  }
}
