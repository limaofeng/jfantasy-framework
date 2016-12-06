package org.jfantasy.autoconfigure;

import com.aliyun.openservices.ons.api.bean.ProducerBean;
import org.jfantasy.aliyun.AliyunSettings;
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

@ComponentScan({"org.jfantasy.invoice","org.jfantasy.member","org.jfantasy.logistics", "org.jfantasy.card", "org.jfantasy.order", "org.jfantasy.pay", "org.jfantasy.trade"})
@Configuration
@EntityScan("org.jfantasy.*.bean")
@EnableConfigurationProperties(PaySettings.class)
public class TradeAutoConfiguration {

    public static final String ONS_TAGS_TRANSACTION = "transaction";
    public static final String ONS_TAGS_ORDER = "order";
    public static final String ONS_TAGS_ACCOUNT = "account";
    public static final String ONS_TAGS_CARDBIND = "card_bind";

    private final AliyunConfiguration aliyunConfiguration;

    @Resource(name = "aliyunSettings")
    private AliyunSettings aliyunSettings;

    @Autowired
    public TradeAutoConfiguration(AliyunConfiguration aliyunConfiguration) {
        this.aliyunConfiguration = aliyunConfiguration;
    }

    @Bean
    public PayProductConfiguration paymentConfiguration() {
        return new PayProductConfiguration();
    }

    @Bean(name = "pay.aliyunSettings")
    @ConfigurationProperties(prefix = "aliyun.ons.pay")
    public AliyunSettings payAliyunSettings() {
        return new AliyunSettings(aliyunSettings);
    }

    /**
     * 发布者 支付相关通知
     */
    @Bean(name = "pay.producer", initMethod = "start", destroyMethod = "shutdown")
    public ProducerBean producer() {
        return aliyunConfiguration.producer(payAliyunSettings());
    }

}
