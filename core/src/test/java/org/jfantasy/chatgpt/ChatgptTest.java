package org.jfantasy.chatgpt;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.UnirestObjectMapper;
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
