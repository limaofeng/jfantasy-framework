package org.jfantasy.framework.spring.validation;

/**
 * 分组操作
 *
 * @author limaofeng
 */
public class Operation {

  public static final Class GET = Get.class;
  public static final Class CREATE = Create.class;
  public static final Class UPDATE = Update.class;
  public static final Class DELETE = Delete.class;

  interface Create {}

  interface Update {}

  interface Delete {}

  interface Get {}
}
