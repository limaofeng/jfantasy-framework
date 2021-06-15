package org.jfantasy.framework.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.json.bean.User;
import org.junit.jupiter.api.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.util.Assert;

public class SpELUtilTest {

    private static final Log LOG = LogFactory.getLog(SpELUtilTest.class);

    @Test
    public void testCreateEvaluationContext() throws Exception {
        User user = new User();
        user.setUsername("limaofeng");

        EvaluationContext context = SpELUtil.createEvaluationContext(user);
        Expression expression = SpELUtil.getExpression(" username == 'limaofeng' ");

        Boolean retVal = expression.getValue(context,Boolean.class);

        LOG.debug("value = " + retVal);

        Assert.isTrue(retVal);

        user.setUsername("hebo");
        context = SpELUtil.createEvaluationContext(user);

        retVal = expression.getValue(context,Boolean.class);

        LOG.debug("value = " + retVal);

        Assert.isTrue(!retVal);

        expression = SpELUtil.getExpression(" true ");

        retVal = expression.getValue(context,Boolean.class);

        LOG.debug("value = " + retVal);

        Assert.isTrue(retVal);
    }

    @Test
    public void testGetExpression() throws Exception {
        User user = new User();
        user.setUsername("aaa");

        user.setEnabled(true);
        EvaluationContext context = SpELUtil.createEvaluationContext(user);
        Expression expression = SpELUtil.getExpression(" username=='aaa'");

        Boolean retVal = expression.getValue(context,Boolean.class);

        LOG.debug("value = " + retVal);

        Assert.isTrue(retVal);
    }

    @Test
    public void testGetSystem() throws Exception {
        EvaluationContext context = SpELUtil.createEvaluationContext();
        Expression expression = SpELUtil.getExpression(" (#systemProperties['spring.profiles.active'] == 'prod' ? '' : 'DEV') ");//['spring.profiles.active']

        String retVal = expression.getValue(context,String.class);

        LOG.debug("value = " + retVal);

        expression = SpELUtil.getExpression("'P' + (#systemProperties['spring.profiles.active'] == 'prod' ? '' : 'DEV')");

        LOG.debug("value = " + expression.getValue(context,String.class));

    }

}