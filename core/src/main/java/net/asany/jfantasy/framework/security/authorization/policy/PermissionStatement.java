package net.asany.jfantasy.framework.security.authorization.policy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import lombok.Data;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContext;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;

@Data
public class PermissionStatement {

  private List<String> action;
  private List<PermissionResource> resource;
  private List<PolicyCondition> conditions;
  private PolicyEffect effect;

  // 缓存通配符和它们对应正则表达式
  private Map<String, Pattern> regexCache = new ConcurrentHashMap<>();

  public boolean appliesToResource(String resourceString) {
    for (PermissionResource res : resource) {
      if (res.appliesToResource(resourceString)) {
        return true;
      }
    }
    return false;
  }

  private Pattern convertWildcardToRegex(String wildcard) {
    StringBuilder s = new StringBuilder(wildcard.length());
    s.append('^');
    for (int i = 0, is = wildcard.length(); i < is; i++) {
      char c = wildcard.charAt(i);
      switch (c) {
        case '*':
          s.append(".*");
          break;
        case '?':
          s.append(".");
          break;
          // Escape special regexp characters
        case '(':
        case ')':
        case '[':
        case ']':
        case '$':
        case '^':
        case '.':
        case '{':
        case '}':
        case '|':
        case '\\':
          s.append("\\");
          // fall through
        default:
          s.append(c);
      }
    }
    s.append('$');
    return Pattern.compile(s.toString());
  }

  public boolean appliesToAction(String action) {
    for (String pattern : this.action) {
      if (actionMatchesPattern(action, pattern)) {
        return true;
      }
    }
    return false;
  }

  private boolean actionMatchesPattern(String action, String pattern) {
    Pattern regex = regexCache.computeIfAbsent(pattern, this::convertWildcardToRegex);
    return RegexpUtil.isMatch(action, regex);
  }

  public boolean isSatisfiedBy(RequestContext context) {
    if (conditions == null) {
      return true;
    }
    return conditions.stream().allMatch(condition -> condition.isSatisfied(context));
  }

  public boolean matches(String resource, String action) {
    return this.appliesToResource(resource) && this.appliesToAction(action);
  }
}
