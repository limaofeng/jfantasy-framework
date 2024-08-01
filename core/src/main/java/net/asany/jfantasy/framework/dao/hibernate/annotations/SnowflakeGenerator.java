package net.asany.jfantasy.framework.dao.hibernate.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import net.asany.jfantasy.framework.dao.hibernate.generator.SnowflakeIdentifierGenerator;
import org.hibernate.annotations.IdGeneratorType;

@IdGeneratorType(SnowflakeIdentifierGenerator.class)
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface SnowflakeGenerator {
  /**
   * 工作机器ID
   *
   * @return 工作机器ID
   */
  long workerId() default 1;

  /**
   * 数据中心ID
   *
   * @return 数据中心ID
   */
  long dataCenterId() default 1;

  /**
   * 格式化
   *
   * @return 格式化
   */
  SnowflakeFormat format() default SnowflakeFormat.NONE;

  /**
   * 长度 默认0 只在 format 为 BASE62 时有效
   *
   * @return 长度
   */
  long length() default 0;

  /**
   * 补位方式
   *
   * @return 补位方式
   */
  PaddingType paddingType() default PaddingType.SUFFIX;
}
