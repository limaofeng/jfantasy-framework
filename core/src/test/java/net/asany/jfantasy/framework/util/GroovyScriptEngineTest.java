/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.util;

import java.nio.file.Paths;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import net.asany.jfantasy.framework.error.User;
import net.asany.jfantasy.framework.util.common.file.FileUtil;
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
