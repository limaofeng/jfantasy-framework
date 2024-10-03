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
package net.asany.jfantasy.framework.spring;

import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.User;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

@Slf4j
public class SpELUtilTest {

  @Test
  public void testCreateEvaluationContext() throws Exception {
    User user = new User();
    user.setUsername("limaofeng");

    EvaluationContext context = SpELUtil.createEvaluationContext(user);
    Expression expression = SpELUtil.getExpression(" username == 'limaofeng' ");

    Boolean retVal = expression.getValue(context, Boolean.class);

    log.debug("value = " + retVal);

    assertTrue(retVal);

    user.setUsername("hebo");
    context = SpELUtil.createEvaluationContext(user);

    retVal = expression.getValue(context, Boolean.class);

    log.debug("value = " + retVal);

    assertTrue(!retVal);

    expression = SpELUtil.getExpression(" true ");

    retVal = expression.getValue(context, Boolean.class);

    log.debug("value = " + retVal);

    assertTrue(retVal);
  }

  @Test
  public void testGetExpression() throws Exception {
    User user = new User();
    user.setUsername("aaa");

    user.setEnabled(true);
    EvaluationContext context = SpELUtil.createEvaluationContext(user);
    Expression expression = SpELUtil.getExpression(" username=='aaa'");

    Boolean retVal = expression.getValue(context, Boolean.class);

    log.debug("value = " + retVal);

    assertTrue(retVal);
  }

  @Test
  public void testGetSystem() throws Exception {
    EvaluationContext context = SpELUtil.createEvaluationContext();
    Expression expression =
        SpELUtil.getExpression(
            " (#systemProperties['spring.profiles.active'] == 'prod' ? '' : 'DEV') "); // ['spring.profiles.active']

    String retVal = expression.getValue(context, String.class);

    log.debug("value = " + retVal);

    expression =
        SpELUtil.getExpression(
            "'P' + (#systemProperties['spring.profiles.active'] == 'prod' ? '' : 'DEV')");

    log.debug("value = " + expression.getValue(context, String.class));
  }
}
