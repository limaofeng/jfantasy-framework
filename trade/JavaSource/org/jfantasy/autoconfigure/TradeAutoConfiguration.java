package org.jfantasy.autoconfigure;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import org.jfantasy.aliyun.AliyunSettings;
import org.jfantasy.order.listener.OrderSaveListener;
import org.jfantasy.pay.product.PaySettings;
import org.jfantasy.pay.service.PayProductConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@ComponentScan({"org.jfantasy.member","org.jfantasy.logistics", "org.jfantasy.card", "org.jfantasy.order", "org.jfantasy.pay", "org.jfantasy.trade"})
@Configuration
@EntityScan("org.jfantasy.*.bean")
@EnableConfigurationProperties(PaySettings.class)
public class TradeAutoConfiguration {

    public static final String ONS_TAGS_TRANSACTION = "transaction";
    public static final String ONS_TAGS_ORDER = "order";
    public static final String ONS_TAGS_ACCOUNT = "account";
    public static final String ONS_TAGS_CARDBIND = "card_bind";

    @Bean
    public PayProductConfiguration paymentConfiguration() {
        return new PayProductConfiguration();
    }

    @Autowired
    private AliyunConfiguration aliyunConfiguration;

    @Resource(name = "aliyunSettings")
    private AliyunSettings aliyunSettings;

    @Bean(name = "pay.aliyunSettings")
    @ConfigurationProperties(prefix = "aliyun.ons.pay")
    public AliyunSettings payAliyunSettings() {
        return new AliyunSettings(aliyunSettings);
    }

    @Bean(name = "order.aliyunSettings")
    @ConfigurationProperties(prefix = "aliyun.ons.order")
    public AliyunSettings orderAliyunSettings() {
        return new AliyunSettings(aliyunSettings);
    }

    /**
     * 发布者 支付相关通知
     */
    @Bean(name = "pay.producer", initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean producer() {
        return aliyunConfiguration.producer(payAliyunSettings());
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean consumer() {
        Map<Subscription, MessageListener> subscriptions = new HashMap<>();
        Subscription key = new Subscription();
        key.setTopic(orderAliyunSettings().getTopicId());
        key.setExpression("*");
        subscriptions.put(key, orderSaveListener());
        return aliyunConfiguration.consumer(orderAliyunSettings(), subscriptions);
    }

    /**
     * 订阅业务订单保存
     *
     * @return OrderSaveListener
     */
    @Bean
    public OrderSaveListener orderSaveListener() {
        return new OrderSaveListener();
    }

}
