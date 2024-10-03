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

import org.apache.commons.codec.binary.Base64;

public class Base64Util {

  private Base64Util() {}

  public static byte[] encode(byte[] data) {
    return Base64.encodeBase64(data);
  }

  public static byte[] decode(byte[] data) {
    return Base64.decodeBase64(data);
  }
}
