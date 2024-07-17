package net.asany.jfantasy.framework.security.authorization.policy;

import lombok.Getter;

@Getter
public enum ResourceActionType {
  LIST("list"),
  WRITE("write"),
  READ("read");
  private final String type;

  ResourceActionType(String type) {
    this.type = type;
  }

  public static ResourceActionType of(String type) {
    for (ResourceActionType value : ResourceActionType.values()) {
      if (value.type.equalsIgnoreCase(type)) {
        return value;
      }
    }
    throw new IllegalArgumentException("Unknown resource action type: " + type);
  }
}
