package org.jfantasy.framework.dao.hibernate.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.JSON;
import org.junit.jupiter.api.Test;

@Slf4j
class MapConverterTest {

  MapConverter<Map<String, List<String>>> mapConverter = new MapConverter<>();

  @Test
  void convertToDatabaseColumn() {
    Map<String, Map<String, List<String>>> condition = testData();

    String jsonStr = mapConverter.convertToDatabaseColumn(condition);

    assertEquals(jsonStr, JSON.serialize(condition));
  }

  @Test
  void convertToEntityAttribute() {
    Map<String, Map<String, List<String>>> condition = testData();

    String jsonStr = JSON.serialize(condition);
    log.debug("json: " + jsonStr);

    Map<String, Map<String, List<String>>> newCondition =
        mapConverter.convertToEntityAttribute(jsonStr);

    assertTrue(newCondition.containsKey("NotIpAddress"));

    Map<String, List<String>> newNotIpAddress = newCondition.get("NotIpAddress");

    assertTrue(newNotIpAddress.containsKey("acs:SourceIp"));

    assertEquals(newNotIpAddress.get("acs:SourceIp").size(), 2);
  }

  private Map<String, Map<String, List<String>>> testData() {
    Map<String, Map<String, List<String>>> condition = new HashMap<>();

    Map<String, List<String>> notIpAddress = new HashMap<>();
    List<String> sourceIps = new ArrayList<>();
    sourceIps.add("192.168.1.1");
    sourceIps.add("192.168.1.2");
    notIpAddress.put("acs:SourceIp", sourceIps);

    condition.put("NotIpAddress", notIpAddress);

    return condition;
  }
}
