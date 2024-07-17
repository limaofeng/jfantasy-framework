package net.asany.jfantasy.framework.security.authorization.config;

import lombok.Data;
import net.asany.jfantasy.framework.security.authorization.policy.PolicyEffect;

@Data
public class DefaultPolicy {
  private PolicyEffect effect;
}
