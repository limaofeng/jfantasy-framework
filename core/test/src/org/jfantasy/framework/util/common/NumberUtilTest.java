package org.jfantasy.framework.util.common;

import org.junit.Test;


public class NumberUtilTest {

    @Test
    public void toHex() throws Exception {
        String id = NumberUtil.toHex(16);
        System.out.println(NumberUtil.toLong(id,16));
    }

}