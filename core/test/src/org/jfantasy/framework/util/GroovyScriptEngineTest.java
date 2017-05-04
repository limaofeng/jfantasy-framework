package org.jfantasy.framework.util;

import org.jfantasy.framework.util.common.file.FileUtil;
import org.jfantasy.framework.util.json.bean.User;
import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class GroovyScriptEngineTest {

    @Test
    public void groovy() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("groovy");
        User user = new User();
        engine.put("user",user);
        user.setUsername("limaofeng");
        Object obj = engine.eval(FileUtil.readFile("test/src/org/jfantasy/framework/util/script_test.groovy"));

        System.out.println(obj);
    }

}
