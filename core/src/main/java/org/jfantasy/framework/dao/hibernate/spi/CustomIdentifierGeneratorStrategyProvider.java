package org.jfantasy.framework.dao.hibernate.spi;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.jpa.spi.IdentifierGeneratorStrategyProvider;
import org.jfantasy.framework.dao.hibernate.generator.SequenceGenerator;
import org.jfantasy.framework.dao.hibernate.generator.SerialNumberGenerator;

public class CustomIdentifierGeneratorStrategyProvider
    implements IdentifierGeneratorStrategyProvider {

  private final Map<String, Class<?>> strategies = new HashMap<>();

  {
    strategies.put("fantasy-sequence", SequenceGenerator.class);
    strategies.put("serialnumber", SerialNumberGenerator.class);
  }

  @Override
  public Map<String, Class<?>> getStrategies() {
    return strategies;
  }
}
