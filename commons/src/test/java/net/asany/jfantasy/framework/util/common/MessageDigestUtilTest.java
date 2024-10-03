/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
