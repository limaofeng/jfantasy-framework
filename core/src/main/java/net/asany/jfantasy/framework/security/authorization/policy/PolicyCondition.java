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
package net.asany.jfantasy.framework.security.authorization.policy;

import java.time.ZonedDateTime;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContext;

@Slf4j
public class PolicyCondition {

  private static final ScriptEngine engine =
      new ScriptEngineManager().getEngineByName("JavaScript");

  private final String condition;

  public PolicyCondition(String condition) {
    this.condition = condition;
  }

  public boolean isSatisfied(RequestContext context) {
    engine.put("sourceIp", context.getSourceIp());
    engine.put("secureTransport", context.isSecureTransport());
    engine.put("currentTimestamp", ZonedDateTime.now());

    try {
      return (boolean) engine.eval(condition);
    } catch (ScriptException e) {
      log.warn("Error evaluating condition: {}", condition, e);
      return false;
    }
  }
}
