package net.asany.jfantasy.framework.util.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

@Slf4j
public class MessageDigestUtilTest {

  @Test
  public void testGet() throws Exception {
    String md5code =
        DigestUtils.md5DigestAsHex(
            new FileInputStream(new File(PathUtil.classes() + "/backup/testconfig/log4j.xml")));
    log.debug("文件获取MD5码:" + md5code);
    InputStream input =
        new FileInputStream(new File(PathUtil.classes() + "/backup/testconfig/log4j.xml"));
    md5code = DigestUtils.md5DigestAsHex(input);
    log.debug("文件流获取MD5码:" + md5code);
  }
}
