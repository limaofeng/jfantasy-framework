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
package net.asany.jfantasy.framework.security.auth.oauth2.jwt;

import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.security.auth.oauth2.JwtTokenPayload;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.apache.commons.codec.binary.Base64;

public class JwtUtils {

  public static JwtTokenPayload payload(String accessToken) {
    String[] strings = StringUtil.tokenizeToStringArray(accessToken, ".");
    return JSON.deserialize(new String(Base64.decodeBase64(strings[1])), JwtTokenPayload.class);
  }
}
