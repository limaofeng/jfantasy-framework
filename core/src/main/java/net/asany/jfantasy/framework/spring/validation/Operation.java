package net.asany.jfantasy.framework.spring.validation;

/**
 * 分组操作
 *
 * @author limaofeng
 */
public interface Operation {

  Class GET = Get.class;
  Class CREATE = Create.class;
  Class UPDATE = Update.class;
  Class DELETE = Delete.class;

  interface Create {}

  interface Update {}

  interface Delete {}

  interface Get {}
}
