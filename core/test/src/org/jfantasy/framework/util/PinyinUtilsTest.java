package org.jfantasy.framework.util;

import org.junit.Before;
import org.junit.Test;

public class PinyinUtilsTest {

    @Before
    public void setUp() throws Exception {
        PinyinUtils.addMutilDict("白术","bái,zhú");
    }

    @Test
    public void getShort() throws Exception {
        System.out.println(PinyinUtils.getShort("白术"));
    }

    @Test
    public void getAll() throws Exception {
        System.out.println(PinyinUtils.getAll("白术"));
    }

}