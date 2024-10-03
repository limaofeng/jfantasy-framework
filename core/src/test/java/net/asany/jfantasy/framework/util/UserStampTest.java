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

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.userstamp.Decoder;
import net.asany.jfantasy.framework.util.userstamp.Encoder;
import net.asany.jfantasy.framework.util.userstamp.UserResult;
import net.asany.jfantasy.framework.util.userstamp.UserStamp;
import org.junit.jupiter.api.Test;

@Slf4j
public class UserStampTest {

  @Test
  public void decode() {
    UserResult result = Decoder.decode("vAcTChtxIcsgJAnI"); // vAcTChtxVAsCVCHe
    assert result != null;
    assert result.checkPassword("1231245");
    log.debug(JSON.serialize(result));
  }

  @Test
  public void encode() {
    UserStamp stamp = Encoder.encode(1, 1123, "1231245", 3, 3);
    log.debug(JSON.serialize(stamp) + "=>" + stamp);
    log.debug(JSON.serialize(Decoder.decode(stamp.toString())));
  }
}
