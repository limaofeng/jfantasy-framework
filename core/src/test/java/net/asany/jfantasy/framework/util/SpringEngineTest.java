package net.asany.jfantasy.framework.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import net.asany.jfantasy.framework.error.User;
import net.asany.jfantasy.framework.spring.SpELUtil;
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
