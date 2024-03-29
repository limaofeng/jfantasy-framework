package org.jfantasy.framework.util;

import java.nio.file.Paths;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.jfantasy.framework.jackson.models.User;
import org.jfantasy.framework.util.common.file.FileUtil;
import org.junit.jupiter.api.Test;

public class GroovyScriptEngineTest {

  @Test
  public void groovy() throws Exception {
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("groovy");
    User user = new User();
    engine.put("user", user);
    user.setUsername("limaofeng");
    String str =
        FileUtil.readString(Paths.get("test/src/org/jfantasy/framework/util/script_test.groovy"));
    Object obj = engine.eval(str);
    System.out.println(obj);
  }
}
