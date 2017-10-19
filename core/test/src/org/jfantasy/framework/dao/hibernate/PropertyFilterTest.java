package org.jfantasy.framework.dao.hibernate;

import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyFilterTest {

    @Test
    public void getPropertyValue() throws Exception {
        PropertyFilter filter = new PropertyFilter("name","limaofeng");
        System.out.println(filter.getPropertyValue());
    }

}