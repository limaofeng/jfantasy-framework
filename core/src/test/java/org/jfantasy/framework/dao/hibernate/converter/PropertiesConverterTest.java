package org.jfantasy.framework.dao.hibernate.converter;

import java.util.Properties;
import org.jfantasy.framework.jackson.JSON;
import org.junit.jupiter.api.Test;

public class PropertiesConverterTest {

  private PropertiesConverter converter = new PropertiesConverter();

  @Test
  public void convertToDatabaseColumn() {
    Properties properties =
        new Properties() {
          {
            this.setProperty("host", "localhost");
            this.put("port", 9090);
          }
        };

    JSON.serialize(properties);
  }

  @Test
  public void convertToEntityAttribute() {
    String json = "{\"host\":\"localhost\",\"port\":9090}";
    JSON.deserialize(json, Properties.class);
  }
}
