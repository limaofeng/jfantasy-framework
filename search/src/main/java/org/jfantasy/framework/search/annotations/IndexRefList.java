package org.jfantasy.framework.search.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示需要嵌入对该RefList对象的索引。结合@IndexRefBy使用。索引域的名称形如“father.name”。
 *
 * @author 李茂峰
 * @since 2013-1-23 下午03:07:20
 * @version 1.0
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexRefList {}
