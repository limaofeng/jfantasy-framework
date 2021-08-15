package org.jfantasy.framework.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.jfantasy.framework.spring.SpELUtil;
import org.jfantasy.framework.util.json.bean.User;
import org.junit.jupiter.api.Test;

public class SpringEngineTest {

  @Test
  public void spring() throws Exception {
    SpELUtil.createEvaluationContext();
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("javascript");
    User user = new User();
    engine.put("user", user);
    user.setUsername("limaofeng");
    Object obj = engine.eval("var i = 0; ++i;user.username;");

    System.out.println(obj);
  }
}
