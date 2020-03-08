package org.jfantasy.framework.dao.hibernate.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.DateUtil;
import org.junit.Test;
import java.util.Date;

public class ReflectionUtilsTest {

    private static final Log LOG = LogFactory.getLog(ReflectionUtilsTest.class);

    @Test
    public void convertStringToObject() throws Exception {
        LOG.debug(DateUtil.now());
        LOG.debug(ReflectionUtils.convertStringToObject("2019-04-08T08:26:00.853Z",Date.class));
    }

}