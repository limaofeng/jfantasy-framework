package org.jfantasy.framework.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.jfantasy.framework.jackson.models.User;
import org.junit.jupiter.api.Test;

public class JavaScriptEngineTest {

  @Test
  public void javascript() throws Exception {
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("javascript");
    User user = new User();
    engine.put("user", user);
    user.setUsername("limaofeng");
    Object obj = engine.eval("var i = 0; ++i;user.username;Date.now()");

    System.out.println(obj);
  }
}
