package net.asany.jfantasy.framework.security.authorization.policy;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContext;

@Data
public class PermissionPolicy {
  private String id;
  private List<String> subjects;
  private List<PermissionStatement> statements;

  public boolean hasPermission(String resource, String action, RequestContext context) {
    return this.statements.stream()
        .filter(statement -> statement.matches(resource, action, context))
        .anyMatch(
            statement -> {
              context.addMatchedRule(action + ":" + resource, statement.getEffect().isAllow());
              return statement.getEffect().isAllow();
            });
  }

  public boolean appliesToSubject(String subject) {
    for (String pattern : subjects) {
      if (subjectMatchesPattern(subject, pattern)) {
        return true;
      }
    }
    return false;
  }

  public boolean matches(String resource, String action, RequestContext context) {
    return this.statements.stream()
        .anyMatch(statement -> statement.matches(resource, action, context));
  }

  public static boolean subjectMatchesPattern(String subject, String pattern) {
    String regex = Pattern.quote(pattern).replace("*", "\\E.*\\Q");
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(subject);
    return m.matches();
  }
}
