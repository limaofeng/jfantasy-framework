package org.jfantasy.framework.jackson.annotation;

public @interface BeanFilter {
    /**
     * 要忽略字段的POJO <br>
     * 2013-9-27 下午4:27:08
     *
     * @return type
     */
    Class<?> type();

    /**
     * 要包含的字段名 <br>
     * 2013-9-27 下午4:27:12
     *
     * @return names
     */
    String[] includes() default {};

    /**
     * 要忽略的字段名 <br>
     *
     * @return names
     */
    String[] excludes() default {};
}
