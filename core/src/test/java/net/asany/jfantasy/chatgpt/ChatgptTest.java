package net.asany.jfantasy.chatgpt;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.jackson.UnirestObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatgptTest {

  @BeforeEach
  void setUp() {
    Unirest.setObjectMapper(new UnirestObjectMapper(JSON.getObjectMapper()));
  }

  @Test
  void newSession() throws UnirestException {
    Chatgpt chatgpt = new Chatgpt("xxxxxxxxxxxxxxxxx");
    chatgpt.newSession();
  }
}
