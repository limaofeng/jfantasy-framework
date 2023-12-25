package net.asany.jfantasy.framework.dao.jpa;

import net.asany.jfantasy.framework.error.ValidationException;

/**
 * 字段未发现
 *
 * @author limaofeng
 */
public class PropertyNotFoundException extends ValidationException {
  public PropertyNotFoundException(String name) {
    super("过滤字段: " + name + " 不存在");
  }

  public PropertyNotFoundException(String name, String message) {
    super("过滤字段：" + name + ", " + message);
  }
}
