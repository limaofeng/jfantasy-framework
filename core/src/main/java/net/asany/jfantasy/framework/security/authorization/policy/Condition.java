package net.asany.jfantasy.framework.security.authorization.policy;

import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContext;

public interface Condition {
  boolean evaluate(RequestContext context);
}
