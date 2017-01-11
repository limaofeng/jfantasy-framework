package org.jfantasy.weixin.framework.account;

import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.weixin.framework.exception.AppidNotFoundException;
import org.jfantasy.weixin.framework.session.WeixinApp;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的微信公众号详情服务类
 */
public class SimpleWeixinAppService implements WeixinAppService {

    private List<WeixinApp> accounts = new ArrayList<WeixinApp>();

    @Override
    public WeixinApp loadAccountByAppid(String appid) throws AppidNotFoundException {
        WeixinApp weixinApp = ObjectUtil.find(accounts,"getAppId()",appid);
        if(weixinApp == null){
            throw new AppidNotFoundException(" appid 不存在 ");
        }
        return weixinApp;
    }

    @Override
    public List<WeixinApp> getAll() {
        return accounts;
    }

    public void setAccounts(List<WeixinApp> accounts) {
        this.accounts = accounts;
    }

}
