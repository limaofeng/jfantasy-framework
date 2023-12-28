package net.asany.jfantasy.chatgpt;

import com.mashape.unirest.http.exceptions.UnirestException;
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
