package org.jfantasy.weixin.framework.session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.weixin.ApplicationTest;
import org.jfantasy.weixin.framework.core.Openapi;
import org.jfantasy.weixin.framework.factory.WeixinSessionFactory;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.framework.message.user.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
@ActiveProfiles("dev")
public class WeixinSessionTest {

    private final static Log LOG = LogFactory.getLog(WeixinSessionTest.class);

    @Autowired
    private WeixinSessionFactory weixinSessionFactory;

    @Test
    public void getOauth2User() throws Exception {
        try {
            WeixinSession weixinSession = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession("wx958ba7d1684203b3"));
            Openapi openapi = weixinSession.getOpenapi();
            User user = openapi.getUser("021FujDQ14SUH61aBLBQ12wyDQ1FujD2");
            LOG.debug(JSON.serialize(user));
        } finally {
            WeixinSessionUtils.closeSession();
        }
    }

}