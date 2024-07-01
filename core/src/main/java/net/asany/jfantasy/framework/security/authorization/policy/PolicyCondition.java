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
