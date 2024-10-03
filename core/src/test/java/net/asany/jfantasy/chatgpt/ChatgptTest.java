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
package net.asany.jfantasy.chatgpt;

import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.jackson.UnirestObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatgptTest {

  @BeforeEach
  void setUp() {
    Unirest.config().setObjectMapper(new UnirestObjectMapper(JSON.getObjectMapper()));
  }

  @Test
  void newSession() throws UnirestException {
    Chatgpt chatgpt = new Chatgpt("xxxxxxxxxxxxxxxxx");
    chatgpt.newSession();
  }
}
