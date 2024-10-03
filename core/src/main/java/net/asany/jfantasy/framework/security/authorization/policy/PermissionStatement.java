/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
  private PolicyEffect effect = PolicyEffect.ALLOW;

  // 缓存通配符和它们对应正则表达式
  private static Map<String, Pattern> REGEX_CACHE = new ConcurrentHashMap<>();

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
    Pattern regex = REGEX_CACHE.computeIfAbsent(pattern, this::convertWildcardToRegex);
    return RegexpUtil.isMatch(action, regex);
  }

  public boolean isSatisfiedBy(RequestContext context) {
    if (conditions == null) {
      return true;
    }
    return conditions.stream().allMatch(condition -> condition.isSatisfied(context));
  }

  public boolean matches(String resource, String action, RequestContext context) {
    return this.appliesToResource(resource)
        && this.appliesToAction(action)
        && this.isSatisfiedBy(context);
  }

  @Override
  public String toString() {
    return "PermissionStatement{"
        + "action="
        + action
        + ", resource="
        + resource
        + ", conditions="
        + conditions
        + ", effect="
        + effect
        + '}';
  }
}
