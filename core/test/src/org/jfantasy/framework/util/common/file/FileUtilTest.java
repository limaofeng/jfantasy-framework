package org.jfantasy.framework.util.common.file;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-09 17:05
 */
public class FileUtilTest {

    @Test
    public void createFolder() {
        System.out.println(new File("/tmp/a/b/c/d").mkdirs());
    }
}