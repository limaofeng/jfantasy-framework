package net.asany.jfantasy.framework.security.authorization.policy;

/**
 * 策略生效的效果
 *
 * @author limaofeng
 */
public enum PolicyEffect {
  /** 允许 */
  ALLOW,
  /** 拒绝 */
  DENY;

  public static PolicyEffect fromString(String text) {
    for (PolicyEffect effect : PolicyEffect.values()) {
      if (effect.name().equalsIgnoreCase(text)) {
        return effect;
      }
    }
    throw new IllegalArgumentException("No constant with text " + text + " found");
  }
}
