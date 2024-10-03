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
package net.asany.jfantasy.framework.util.userstamp;

import java.security.MessageDigest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Decoder {

  private Decoder() {}

  public static UserResult decode(String userStamp) {
    if (userStamp == null) {
      if (log.isDebugEnabled()) {
        log.debug("userStamp is NULL ");
      }
      return null;
    }
    char[] stamp = userStamp.toCharArray();
    if ((stamp.length != 16) || (!v(stamp))) {
      if (log.isDebugEnabled()) {
        log.debug("无效的userStamp : " + userStamp);
      }
      return null;
    }
    UserResult userResult = new UserResult();
    int randomType = CToN(stamp[6]);
    userResult.setUserType((randomType & 0x30) >> 4);
    randomType &= 15;
    userResult.setRandomType(randomType);
    userResult.setCssStyle(CToN(stamp[13]) >> 3);
    char[] pwChars = new char[5];
    for (int i = 0; i < 5; i++) {
      pwChars[i] = stamp[RandomType.SEQUENCE[randomType][i]];
    }
    userResult.setPasswordHash(String.valueOf(pwChars));
    int idStop = CToN(stamp[2]) & 0x7;
    int userId = 0;
    for (int i = idStop + 4; i >= 5; i--) {
      userId = userId * 62 + CToN(stamp[RandomType.SEQUENCE[randomType][i]]);
    }
    userResult.setUserId(userId);
    return userResult;
  }

  public static boolean v(char[] userStamp) {
    try {
      MessageDigest algorithm = MessageDigest.getInstance("MD5");
      algorithm.reset();
      algorithm.update((new String(userStamp, 0, 14) + "ldg").getBytes());
      byte[] messageDigest = algorithm.digest();
      for (int i = 0; i < 2; i++) {
        if (userStamp[14 + i] != Encoder.NToC(Math.abs(messageDigest[i]) % 62)) {
          return false;
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return false;
    }
    return true;
  }

  protected static int CToN(char c) {
    return c
        - ((c >= '0') && (c <= '9')
            ? '￼'
            : (c >= 'A') && (c <= 'Z') ? '\'' : (c >= 'a') && (c <= 'z') ? 'a' : c);
  }
}
