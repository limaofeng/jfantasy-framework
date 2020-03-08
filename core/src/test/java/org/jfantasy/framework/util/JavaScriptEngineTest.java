package org.jfantasy.framework.util;

import org.jfantasy.framework.util.common.file.FileUtil;
import org.jfantasy.framework.util.json.bean.User;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JavaScriptEngineTest {

    @Test
    public void javascript() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("javascript");
        User user = new User();
        engine.put("user",user);
        user.setUsername("limaofeng");
        Object obj = engine.eval("var i = 0; ++i;user.username;Date.now()");

        System.out.println(obj);
    }

}
