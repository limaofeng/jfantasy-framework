package org.jfantasy.framework.util.common;

import java.util.Enumeration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

@Slf4j
public class PropertiesHelperTest {

  @Test
  public void testLoad() throws Exception {}

  @Test
  public void testGetProperties() throws Exception {}

  @Test
  public void testSetProperties() throws Exception {}

  @Test
  public void testGetRequiredString() throws Exception {}

  @Test
  public void testGetNullIfBlank() throws Exception {}

  @Test
  public void testGetNullIfEmpty() throws Exception {}

  @Test
  public void testGetAndTryFromSystem() throws Exception {}

  @Test
  public void testGetInteger() throws Exception {}

  @Test
  public void testGetInt() throws Exception {}

  @Test
  public void testGetRequiredInt() throws Exception {}

  @Test
  public void testGetLong() throws Exception {}

  @Test
  public void testGetRequiredLong() throws Exception {}

  @Test
  public void testGetBoolean() throws Exception {}

  @Test
  public void testGetBoolean1() throws Exception {}

  @Test
  public void testGetRequiredBoolean() throws Exception {}

  @Test
  public void testGetFloat() throws Exception {}

  @Test
  public void testGetRequiredFloat() throws Exception {}

  @Test
  public void testGetDouble() throws Exception {}

  @Test
  public void testGetRequiredDouble() throws Exception {}

  @Test
  public void testSetProperty() throws Exception {}

  @Test
  public void testGetProperty() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");

    // 普通测试
    log.debug(helper.getProperty("test"));
    Assert.isTrue(helper.getProperty("test") == "limaofeng");

    // 测试默认值
    log.debug(helper.getProperty("test1", "limaofeng"));
    Assert.isTrue(helper.getProperty("test1", "limaofeng") == "limaofeng");

    // 使用环境变量
    System.setProperty("username", "limaofeng");
    log.debug("System getProperty by key(username) = " + System.getProperty("username"));
    log.debug("System getenv by key(username) = " + System.getenv("username"));
    log.debug(helper.getProperty("testenv"));

    Assert.isTrue(helper.getProperty("testenv") == "username=limaofeng");
    System.setProperty("username", "haolue");
    log.debug(helper.getProperty("testenv"));

    Assert.isTrue(helper.getProperty("testenv") == "username=haolue");
  }

  @Test
  public void testClear() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");

    log.debug(helper.getProperty("test"));
    Assert.isTrue(helper.getProperty("test") == "limaofeng");

    helper.clear();

    log.debug(helper.getProperty("test"));
    Assert.notNull(helper.getProperty("test"));
  }

  @Test
  public void testEntrySet() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");

    for (Map.Entry<Object, Object> entry : helper.getProperties().entrySet()) {
      log.debug(entry.getKey() + " = " + entry.getValue());
    }
  }

  @Test
  public void testPropertyNames() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");

    Enumeration<?> enumeration = helper.getProperties().propertyNames();
    while (enumeration.hasMoreElements()) {
      Object key = enumeration.nextElement();
      log.debug(key + " = " + helper.getProperties().get(key));
    }
  }

  @Test
  public void testContains() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");
    Assert.isTrue(helper.getProperties().contains("limaofeng"));
  }

  @Test
  public void testContainsKey() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");
    Assert.isTrue(helper.getProperties().containsKey("test"));
  }

  @Test
  public void testContainsValue() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");
    Assert.isTrue(helper.getProperties().containsValue("limaofeng"));
  }

  @Test
  public void testElements() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");
    Enumeration<?> enumeration = helper.getProperties().elements();
    while (enumeration.hasMoreElements()) {
      Object value = enumeration.nextElement();
      log.debug(value.toString());
    }
  }

  @Test
  public void testIsEmpty() throws Exception {
    PropertiesHelper helper =
        PropertiesHelper.load("backup/testconfig/props/application.properties");
    Assert.isTrue(!helper.getProperties().isEmpty());
    helper = PropertiesHelper.load("backup/testconfig/props/application_empty.properties");
    Assert.isTrue(helper.getProperties().isEmpty());
  }
}
