package org.jfantasy.framework.util.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.service.FTPService;
import org.jfantasy.framework.util.common.file.FileUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.junit.Test;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileUtilTest {

    private static final Log LOG = LogFactory.getLog(FileUtilTest.class);

    @Test
    public void fileSize() {
        System.out.println(FileUtil.fileSize(1024 + 1024));
    }

    public void ftp() {
//        final FTPFileManager fileManager = new FTPFileManager();
        FTPService ftpService = new FTPService();
        ftpService.setHostname("192.168.199.1");
        ftpService.setUsername("lmf");
        ftpService.setPassword("123456");
//        fileManager.setFtpService(ftpService);
    }

    public void systemProperty() {

        System.out.println(System.getProperty("java.io.tmpdir"));

        System.out.println(File.pathSeparator);

        System.out.println(File.separator);

        System.out.println(File.pathSeparatorChar);

        System.out.println(File.separatorChar);

    }

    @Test
    public void testGetMimeType() throws Exception {
        LOG.debug(FileUtil.getMimeType(FileUtilTest.class.getResourceAsStream("FileUtilTest.class")));
    }

    @Test
    public void testWriteFile() throws Exception {
        String basePath = "/Users/limaofeng/framework/core/test/src/org/jfantasy/framework/util/common/";
        String file = FileUtil.readFile(basePath + "20170503_report.json");
        Map<String,Object> data = JSON.deserialize(file, HashMap.class);
        String base64 = OgnlUtil.getInstance().getValue("data.ecgData",data);
        FileUtil.writeFile(Base64Utils.decodeFromString(base64),basePath + "test.jpg");

        //ImageUtil.write(ImageUtil.getImage(base64),basePath + "/test.jpg");
    }
}
