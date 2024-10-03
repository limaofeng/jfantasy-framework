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
package net.asany.jfantasy.framework.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.ImageProcessingException;
import org.junit.jupiter.api.Test;

@Slf4j
class ImagesTest {

  @Test
  void base64() throws IOException {
    String base64 =
        Images.base64("/Users/limaofeng/Workspace/ai-prime/ehs-python-release/20221017T1100.jpg");
    System.out.println(base64);
    Files.write(Paths.get("/Users/limaofeng/Downloads/20221017T1100.txt"), base64.getBytes());
  }

  @Test
  void resize() throws ImageProcessingException {
    String target = Images.resize("/Users/limaofeng/Downloads/sample.jpg", "150x150");
    assert new File(target).length() > 0;
    log.debug(target);
    Images.ImageMetadata metadata = Images.identify(target);
    log.info(metadata.toString());
  }

  @Test
  void identify() throws ImageProcessingException {
    Images.ImageMetadata metadata = Images.identify("/Users/limaofeng/Downloads/sample.jpg");
    log.debug(metadata.toString());

    int r = metadata.getChannelStatistics().getRed().getMean();
    int g = metadata.getChannelStatistics().getGreen().getMean();
    int b = metadata.getChannelStatistics().getBlue().getMean();

    double luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;

    if (luma < 40) {
      log.debug("图片偏黑");
    }
  }
}
