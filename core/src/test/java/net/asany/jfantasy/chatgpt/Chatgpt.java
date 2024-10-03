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

import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Chatgpt {

  private final String aipKey;
  private static final String baseUrl = "https://api.openai.com/v1/chat";

  public Chatgpt(String apiKey) {
    this.aipKey = apiKey;
  }

  public void newSession() throws UnirestException {
    log.info("newSession");

    //    HttpResponse<String> body =
    //        Unirest.post(baseUrl + "/completions")
    //            .header("Content-Type", "application/json")
    //            .header("Authorization", "Bearer " + aipKey)
    //            .body(
    //                "{\n"
    //                    + "     \"model\": \"gpt-3.5-turbo\",\n"
    //                    + "     \"messages\": [{\"role\": \"user\", \"content\": \"Say this is a
    // test!\"}],\n"
    //                    + "     \"temperature\": 0.7\n"
    //                    + "   }")
    //            .asString();

    //    log.info("body: {}", body.getBody());
  }
}
