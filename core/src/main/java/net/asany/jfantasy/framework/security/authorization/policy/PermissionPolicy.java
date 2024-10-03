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
