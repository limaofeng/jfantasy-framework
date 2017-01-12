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

    public Fans get(String appId,String openId) {
        return fansDao.get(new UserKey(appId,openId));
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

    public Fans save(String id, User user) {
        Fans fans = this.fansDao.get(UserKey.newInstance(id, user.getOpenId()));
        if (fans == null) {
            fans = new Fans();
            fans.setAppId(id);
        }
        return this.fansDao.save(merge(fans, user));
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
        ui = merge(new Fans(), user);
        ui.setAppId(appid);
        this.fansDao.save(ui);
        return ui;
    }


    /**
     * 转换user到userinfo
     *
     * @param fans 微信粉丝
     * @param user 微信平台对象
     * @return Fans
     */
    private Fans merge(Fans fans, User user) {
        fans.setOpenId(user.getOpenId());
        fans.setAvatar(user.getAvatar());
        fans.setCity(user.getCity());
        fans.setCountry(user.getCountry());
        fans.setProvince(user.getProvince());
        fans.setLanguage(user.getLanguage());
        fans.setNickname(user.getNickname());
        fans.setSex(user.getSex());
        fans.setSubscribe(user.isSubscribe());
        if (user.getSubscribeTime() != null) {
            fans.setSubscribeTime(user.getSubscribeTime().getTime());
        }
        fans.setUnionId(user.getUnionid());
        return fans;
    }

    public Fans checkCreateMember(String appid, String openId) throws WeixinException {
        return refresh(appid, openId);
    }

}
