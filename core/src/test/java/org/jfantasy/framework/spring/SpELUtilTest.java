package org.jfantasy.framework.spring;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.util.Assert;

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

    Assert.isTrue(retVal);

    user.setUsername("hebo");
    context = SpELUtil.createEvaluationContext(user);

    retVal = expression.getValue(context, Boolean.class);

    log.debug("value = " + retVal);

    Assert.isTrue(!retVal);

    expression = SpELUtil.getExpression(" true ");

    retVal = expression.getValue(context, Boolean.class);

    log.debug("value = " + retVal);

    Assert.isTrue(retVal);
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

    Assert.isTrue(retVal);
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
