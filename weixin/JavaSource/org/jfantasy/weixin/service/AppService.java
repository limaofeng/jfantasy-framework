package org.jfantasy.weixin.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.weixin.framework.account.WeixinAppService;
import org.jfantasy.weixin.bean.App;
import org.jfantasy.weixin.dao.AppDao;
import org.jfantasy.weixin.framework.exception.AppidNotFoundException;
import org.jfantasy.weixin.framework.session.WeixinApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据库存储微信信息
 */
@Service("weixin.AppService")
@Transactional
public class AppService implements WeixinAppService {

    private final AppDao appDao;

    @Autowired
    public AppService(AppDao appDao) {
        this.appDao = appDao;
    }

    @Override
    public WeixinApp loadAccountByAppid(String appid) throws AppidNotFoundException {
        WeixinApp weixinApp = get(appid);
        if (weixinApp == null) {
            throw new AppidNotFoundException(" appid 不存在 ");
        }
        return weixinApp;
    }

    /**
     * 查找所有配置信息
     *
     * @return List<AccountDetails>
     */
    public List<WeixinApp> getAll() {
        return (List) appDao.getAll();
    }

    /**
     * 列表查询
     *
     * @param pager   分页
     * @param filters 查询条件
     * @return 分页对象
     */
    public Pager<App> findPager(Pager<App> pager, List<PropertyFilter> filters) {
        return this.appDao.findPager(pager, filters);
    }

    /**
     * 保存微信配置信息对象
     *
     * @param wc appid
     * @return 微信配置信息对象
     */
    public App save(App wc) {
        return appDao.save(wc);
    }

    /**
     * 根据id 批量删除
     *
     * @param ids appid
     */
    public void delete(String... ids) {
        for (String id : ids) {
            App m = this.appDao.get(id);
            this.appDao.delete(m);
        }
    }

    /**
     * 通过id查找微信配置对象
     *
     * @param id appid
     * @return 微信配置对象
     */
    public App get(String id) {
        return this.appDao.get(id);
    }

}
