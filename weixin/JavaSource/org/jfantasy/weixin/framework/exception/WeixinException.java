package org.jfantasy.weixin.framework.exception;


import me.chanjar.weixin.common.bean.result.WxError;
import me.chanjar.weixin.common.exception.WxErrorException;

/**
 * 微信异常
 * Created by zzzhong on 2014/12/4.
 */
public class WeixinException extends Exception {

    private WxErrorInfo error;

    public WeixinException(){}
    public WeixinException(WxErrorInfo error) {
        super(error.toString());
        this.error = error;
    }

    public WeixinException(String message) {
        super(message);
    }

    public WeixinException(String message, Exception e) {
        super(message, e);
    }

    public static WeixinException wxExceptionBuilder(WxErrorException e) {
        WxErrorInfo wxError = new WxErrorInfo();
        WxError error = e.getError();
        wxError.setErrorCode(error.getErrorCode());
        wxError.setErrorMsg(error.getErrorMsg());
        wxError.setJson(error.getJson());
        return new WeixinException(wxError);
    }

    public WxErrorInfo getError() {
        return error;
    }

    public void setError(WxErrorInfo error) {
        this.error = error;
    }

}
