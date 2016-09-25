package org.jfantasy.wx.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.security.bean.enums.Sex;
import org.jfantasy.wx.bean.User;
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
public class UserServiceTest {

    private static final Log logger = LogFactory.getLog(User.class);
    @Autowired
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        testSave();
    }

    @After
    public void tearDown() throws Exception {
        testDelete();
    }


    public void testSave() throws Exception {
        User ui = new User();
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

    public Pager<User> testFindPager(String... openid) {
        Pager<User> pager = new Pager<User>();
        List<PropertyFilter> list = new ArrayList<PropertyFilter>();
        if (openid.length > 0) list.add(new PropertyFilter("NES_openId", openid[0]));
        return userService.findPager(pager, list);

    }

}