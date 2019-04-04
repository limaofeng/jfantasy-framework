package org.jfantasy.framework.dao;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-04 15:21
 */
public class PagerTest {

    @Test
    public void builder() {
        Pager.newPager().currentPage(0).orderBy(OrderBy.builder().by("name").order("desc").build()).build();
    }
}