package org.jfantasy.weixin.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.weixin.bean.Fans;
import org.jfantasy.weixin.bean.UserKey;
import org.jfantasy.weixin.dao.FansDao;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.framework.message.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class FansService {

    private final FansDao fansDao;

    @Autowired
    public FansService(FansDao fansDao) {
        this.fansDao = fansDao;
    }

    public Fans get(UserKey key) {
        return fansDao.get(key);
    }

    public Fans save(Fans ui) {
        return fansDao.save(ui);
    }

    public void delete(UserKey... keys) {
        for (UserKey key : keys) {
            fansDao.delete(key);
        }
    }

    public void delete(UserKey key) {
        this.fansDao.delete(key);
    }

    public Pager<Fans> findPager(Pager<Fans> pager, List<PropertyFilter> filters) {
        return this.fansDao.findPager(pager, filters);
    }

    /**
     * 通过openId刷新用户信息
     */
    private Fans refresh(String appid, String openId) throws WeixinException {
        Fans ui = get(UserKey.newInstance(appid, openId));
        if (ui != null) {
            return ui;
        }
        User user = WeixinSessionUtils.getCurrentSession().getUser(openId);
        if (user == null) {
            return null;
        }
        ui = transfiguration(user);
        ui.setAppId(appid);
        this.fansDao.save(ui);
        return ui;
    }


    /**
     * 转换user到userinfo
     *
     * @param u
     * @return Fans
     */
    public Fans transfiguration(User u) {
        if (u == null) {
            return null;
        }
        Fans user = new Fans();
        user.setOpenId(u.getOpenId());
        user.setAvatar(u.getAvatar());
        user.setCity(u.getCity());
        user.setCountry(u.getCountry());
        user.setProvince(u.getProvince());
        user.setLanguage(u.getLanguage());
        user.setNickname(u.getNickname());
        user.setSex(u.getSex());
        user.setSubscribe(u.isSubscribe());
        if (u.getSubscribeTime() != null) {
            user.setSubscribeTime(u.getSubscribeTime().getTime());
        }
        user.setUnionId(u.getUnionid());
        return user;
    }

    public Fans checkCreateMember(String appid, String openId) throws WeixinException {
        return refresh(appid, openId);
    }

}
