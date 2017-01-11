package org.jfantasy.weixin.framework.intercept;


import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.WeixinMessage;

public interface Invocation {

    WeixinMessage invoke() throws WeixinException;

}
