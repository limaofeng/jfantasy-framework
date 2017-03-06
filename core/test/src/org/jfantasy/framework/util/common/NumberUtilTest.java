package org.jfantasy.framework.util.common;

import org.junit.Test;


public class NumberUtilTest {

    @Test
    public void toHex() throws Exception {
        String id = NumberUtil.toHex(1283*20170103);
        System.out.println(id);
        System.out.println(new String(Base64Util.encode((id+"--"+StringUtil.generateNonceString(6)).getBytes())));
        System.out.println(new String(Base64Util.encode("articles--1284--articles".getBytes())));
        System.out.println(new String(Base64Util.encode("articles--0001--articles".getBytes())));
        System.out.println(NumberUtil.toLong(id,16));
    }

}