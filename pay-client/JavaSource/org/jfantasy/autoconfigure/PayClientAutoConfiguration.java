package org.jfantasy.autoconfigure;

import org.jfantasy.aliyun.AliyunSettings;
import org.jfantasy.order.OrderServiceByClient;
import org.jfantasy.rpc.config.NettyClientSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

@Configuration
@Import(OrderServiceByClient.class)
public class PayClientAutoConfiguration {

    @Autowired
    private AliyunConfiguration aliyunConfiguration;

    @Resource(name = "aliyunSettings")
    private AliyunSettings aliyunSettings;

    @Bean(name = "pay.aliyunSettings")
    @ConfigurationProperties(prefix = "aliyun.ons.pay")
    public AliyunSettings aliyunSettings() {
        return new AliyunSettings(aliyunSettings);
    }

    @Bean(name = "order.aliyunSettings")
    @ConfigurationProperties(prefix = "aliyun.ons.order")
    public AliyunSettings orderAliyunSettings() {
        return new AliyunSettings(aliyunSettings);
    }

    @Bean
    @ConfigurationProperties(prefix = "netty.client.pay")
    public NettyClientSettings nettyClientSettings() {
        return new NettyClientSettings();
    }

}
