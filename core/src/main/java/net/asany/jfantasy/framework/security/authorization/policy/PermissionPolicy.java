package net.asany.jfantasy.framework.security.authorization.policy;

import java.util.List;
import lombok.Data;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContext;

@Data
public class PermissionPolicy {
  private String id;
  private List<String> subjects;
  private List<PermissionStatement> statements;

  public boolean hasPermission(String resource, String action, RequestContext context) {
    return this.statements.stream()
        .filter(statement -> statement.matches(resource, action))
        .anyMatch(statement -> statement.isSatisfiedBy(context));
  }

  public boolean appliesToSubject(String subject) {
    for (String pattern : subjects) {
      if (subjectMatchesPattern(subject, pattern)) {
        return true;
      }
    }
    return false;
  }

  public boolean appliesToResource(String resource) {
    return this.statements.stream().anyMatch(statement -> statement.appliesToResource(resource));
  }

  private boolean subjectMatchesPattern(String subject, String pattern) {
    if (pattern.equals("user:*")) {
      return subject.startsWith("user:");
    } else if (pattern.equals("role:*")) {
      return subject.startsWith("role:");
    }
    // 可以添加更多模式匹配逻辑
    return false;
  }
}
