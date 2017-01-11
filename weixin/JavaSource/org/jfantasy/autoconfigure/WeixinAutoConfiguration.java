package org.jfantasy.autoconfigure;

import org.jfantasy.weixin.framework.factory.WeixinSessionFactoryBean;
import org.jfantasy.weixin.framework.message.EventMessage;
import org.jfantasy.weixin.listener.SubscribeListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.jfantasy.weixin")
@EntityScan("org.jfantasy.weixin.bean")
public class WeixinAutoConfiguration {

    private final ApplicationContext applicationContext;

    @Autowired
    public WeixinAutoConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SubscribeListener subscribeListener() {
        return new SubscribeListener();
    }

    @Bean
    public WeixinSessionFactoryBean weiXinSessionFactoryBean() {
        WeixinSessionFactoryBean weixinSessionFactoryBean = new WeixinSessionFactoryBean();
        weixinSessionFactoryBean.setApplicationContext(applicationContext);

        //关注时,记录粉丝信息
        weixinSessionFactoryBean.addEventListener(EventMessage.EventType.subscribe,subscribeListener());

        return weixinSessionFactoryBean;
    }

}
