package org.jfantasy.graphql.inputs;

import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.dao.jpa.PropertyFilterBuilder;

import java.util.List;

/**
 * Input Filter 查询根类
 *
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/4/14 10:07 上午
 */
public class QueryFilter {

    protected PropertyFilterBuilder builder = new PropertyFilterBuilder();

    public PropertyFilterBuilder getBuilder() {
        return this.builder;
    }

    public List<PropertyFilter> build() {
        return this.builder.build();
    }
}
