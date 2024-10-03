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
package net.asany.jfantasy.framework.security.oauth2;

import net.asany.jfantasy.framework.security.auth.AuthType;
import org.junit.jupiter.api.Test;

class AuthTypeTest {

  @Test
  void valueOf() {
    String jwt =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MDQzNDkzNjgsIm5hbWUiOiJsaW1hb2ZlbmciLCJlbWFpbCI6ImxpbWFvZmVuZ0Btc24uY29tIiwidXNlcl9pZCI6MX0.Gx2b-UgKONUVjI2yPMCit2GI7Dtc5F-X5OzoVSpRISk";
    AuthType.of(jwt);
  }
}
