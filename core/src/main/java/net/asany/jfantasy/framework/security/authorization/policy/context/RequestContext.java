package net.asany.jfantasy.framework.security.authorization.policy.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class RequestContext {
  private String userId;
  private String username;
  private Collection<String> roles;
  private String sourceIp;
  private boolean secureTransport;

  @lombok.Builder.Default private transient List<RuleMatchResult> matchedRules = new ArrayList<>();

  @Data
  @lombok.Builder
  public static class RuleMatchResult {
    private String description;
    private boolean result;

    @Override
    public String toString() {
      return description + " : " + result;
    }
  }

  public void addMatchedRule(String description, boolean result) {
    matchedRules.add(new RuleMatchResult(description, result));
  }
}
