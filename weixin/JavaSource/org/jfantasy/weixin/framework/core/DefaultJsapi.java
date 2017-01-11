package org.jfantasy.weixin.framework.core;

import org.jfantasy.weixin.framework.exception.WeixinException;

public class DefaultJsapi implements Jsapi {

    private WeixinService weiXinService;

    protected DefaultJsapi(final WeixinService weiXinService) {
        this.weiXinService = weiXinService;
    }

    @Override
    public String getTicket() throws WeixinException {
        return weiXinService.getJsapiTicket();
    }

    @Override
    public String getTicket(boolean forceRefresh) throws WeixinException {
        return weiXinService.getJsapiTicket(forceRefresh);
    }

    @Override
    public Signature signature(String url) throws WeixinException {
        return weiXinService.createJsapiSignature(url);

    }

}
