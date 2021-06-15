package org.jfantasy.framework.crypto;

import org.junit.jupiter.api.Test;


public class DESUtilTest {
    @Test
    public void encrypt() throws Exception {
        String source = "市规土局所属事业单位招聘20名工作人员";//"amigoxie";
        System.out.println("原文: " + source);
        String key = "A1B2C3D4E5F60708";
        String encryptData = DESUtil.encrypt(source, key);
        System.out.println("加密后: " + encryptData);
        String decryptData = DESUtil.decrypt(encryptData, key);
        System.out.println("解密后: " + decryptData);
    }

    @Test
    public void decrypt() throws Exception {
        DESPlus desPlus = new DESPlus("hooluesoft");
        System.out.println(desPlus.decrypt("76e7d85003fc8963592483e28a53c290"));
        System.out.println(desPlus.encrypt("15921884771"));
    }

}