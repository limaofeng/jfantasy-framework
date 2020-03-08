package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.junit.Test;

public class PropertyFilterTest {

    @Test
    public void getPropertyValue() throws Exception {
        PropertyFilter filter = new PropertyFilter(PropertyFilter.MatchType.EQ,"name","limaofeng");
        System.out.println(filter.getPropertyValue(String.class));
    }

}