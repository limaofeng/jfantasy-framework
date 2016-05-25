package org.jfantasy.framework.autoconfigure;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import org.jfantasy.pay.ons.PayMessageListener;
import org.jfantasy.pay.order.OrderProcessor;
import org.jfantasy.pay.order.OrderServiceRegistry;
import org.jfantasy.pay.order.OrderServiceRegistryRunner;
import org.jfantasy.rpc.client.NettyClientFactory;
import org.jfantasy.rpc.proxy.RpcProxyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@Import(OrderServiceRegistryRunner.class)
public class PayClientAutoConfiguration {

    @Value("${api.pay.rpc.host:127.0.0.1}")
    private String host;
    @Value("${api.pay.rpc.port:9090}")
    private int port;

    @Bean(name = "orderServiceRegistry")
    public OrderServiceRegistry buildOrderServiceRegistry() {
        RpcProxyFactory rpcProxyFactory = new RpcProxyFactory(new NettyClientFactory(host, port));
        return rpcProxyFactory.proxyBean(OrderServiceRegistry.class, 10000);
    }

    @Bean(name = "orderProcessor")
    public OrderProcessor buildOrderProcessor() {
        RpcProxyFactory rpcProxyFactory = new RpcProxyFactory(new NettyClientFactory(host, port));
        return rpcProxyFactory.proxyBean(OrderProcessor.class, 10000);
    }

    @Value("${aliyun.ons.consumerId:CID-20160428}")
    private String consumerId;
    @Value("${aliyun.ons.accessKey:GjYnEEMsLVTomMzF}")
    private String accessKey;
    @Value("${aliyun.ons.secretKey:rYSFhN67iXR0vl0pUSatSQjEqR2e2F}")
    private String secretKey;

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public ConsumerBean consumer() {
        ConsumerBean consumerBean = new ConsumerBean();
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.ConsumerId, consumerId);
        properties.setProperty(PropertyKeyConst.AccessKey, accessKey);
        properties.setProperty(PropertyKeyConst.SecretKey, secretKey);
        consumerBean.setProperties(properties);
        Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
        Subscription key = new Subscription();
        key.setTopic("TopicTestONS1985");
        key.setExpression("pay");
        subscriptionTable.put(key, payMessageListener());
        consumerBean.setSubscriptionTable(subscriptionTable);
        return consumerBean;
    }

    @Bean
    public PayMessageListener payMessageListener() {
        return new PayMessageListener();
    }

}
