package org.jfantasy.weixin.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.weixin.bean.Fans;
import org.jfantasy.weixin.bean.enums.Sex;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class FansServiceTest {

    private static final Log logger = LogFactory.getLog(Fans.class);
    @Autowired
    private FansService fansService;

    @Before
    public void setUp() throws Exception {
        testSave();
    }

    @After
    public void tearDown() throws Exception {
        testDelete();
    }


    public void testSave() throws Exception {
        Fans ui = new Fans();
        ui.setOpenId("test");
        ui.setSex(Sex.female);
        ui.setLastMessageTime(1L);
        ui.setCity("上海");
        ui.setLanguage("zh");
        ui.setNickname("测试nick");
        ui.setSubscribeTime(new Date().getTime());
    }

    public void testDelete() throws Exception {
        //iUserInfoService.deleteByOpenId("test");
    }

    public Pager<Fans> testFindPager(String... openid) {
        Pager<Fans> pager = new Pager<Fans>();
        List<PropertyFilter> list = new ArrayList<PropertyFilter>();
        if (openid.length > 0) list.add(new PropertyFilter("NES_openId", openid[0]));
        return fansService.findPager(pager, list);

    }

}