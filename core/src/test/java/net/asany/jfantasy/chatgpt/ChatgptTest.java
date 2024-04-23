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
