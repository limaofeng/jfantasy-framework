package org.jfantasy.framework.search.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * 标注需要索引的字段<br>
 * 支持的数据类型包括：String、char、boolean、int、long、float、double、Date等基本数据类型。<br>
 * 还支持上述基本数据类型组成的数组、List、Set等。这些集合中的元素，不管是什么数据类型，都会连结成一个字符串，然后加以索引。
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-23 下午03:01:02
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexProperty {
  /**
   * Alias for {@link #name}.
   *
   * @return String
   */
  @AliasFor("name")
  String value() default "";

  /**
   * The <em>name</em> 用于在文档中存储字段。
   *
   * <p>如果未设置，则使用注释属性的名称
   *
   * @since 3.2
   */
  @AliasFor("value")
  String name() default "";

  /**
   * 定义属性在文档中的存储类型
   *
   * @return FieldType
   */
  FieldType type() default FieldType.Auto;

  /**
   * 是否为字段建立索引
   *
   * @return boolean
   */
  boolean index() default true;

  /**
   * 日期字段格式化方式
   *
   * @return DateFormat[]
   */
  DateFormat[] format() default {DateFormat.date_optional_time, DateFormat.epoch_millis};

  /**
   * boolean型，表示是否需要存储，缺省值为 false
   *
   * @return boolean
   */
  boolean store() default false;

  boolean fielddata() default false;

  /**
   * 分词器
   *
   * @return String
   */
  String analyzer() default "";
}
