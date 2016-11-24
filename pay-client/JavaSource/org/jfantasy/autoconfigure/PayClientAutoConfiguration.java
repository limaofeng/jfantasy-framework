package org.jfantasy.autoconfigure;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import org.jfantasy.aliyun.AliyunSettings;
import org.jfantasy.order.OrderServiceByClient;
import org.jfantasy.order.PayMessageListener;
import org.jfantasy.rpc.config.NettyClientSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    @Bean(name = "order.producer", initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean producer() {
        return aliyunConfiguration.producer(orderAliyunSettings());
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean consumer() {
        Map<Subscription, MessageListener> subscriptions = new HashMap<>();
        Subscription key = new Subscription();
        key.setTopic(aliyunSettings().getTopicId());
        key.setExpression("*");
        subscriptions.put(key, payMessageListener());
        return aliyunConfiguration.consumer(aliyunSettings(), subscriptions);
    }

    @Bean
    public PayMessageListener payMessageListener() {
        return new PayMessageListener();
    }

}
