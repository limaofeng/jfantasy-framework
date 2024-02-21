package net.asany.jfantasy.framework.security.authorization.policy.config;

import lombok.Data;
import net.asany.jfantasy.framework.security.authorization.policy.PolicyEffect;

@Data
public class DefaultPolicy {
  private PolicyEffect effect;
}
