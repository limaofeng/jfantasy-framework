package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import org.jfantasy.aliyun.api.Consumer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class ConsumerBeanTest {

    private Consumer consumer;

    @Before
    public void setUp() throws Exception {
        CloudAccount account = new CloudAccount("44IzFLqkj8Pw2YOi", "XexMYOXZGBrPgQTUq4HtKuxxl9zMx1", "http://1744260525350210.mns.cn-hangzhou.aliyuncs.com");
        MNSClient client = account.getMNSClient();

        consumer = new ConsumerBean(client, "dev-notification", "test-platform", "test-platform");
        consumer.start();
    }

    @After
    public void tearDown() throws Exception {
        consumer.shutdown();
    }

    @Test
    public void on() throws Exception {
        consumer.on(message -> {
            System.out.println(message.getBody());
            return true;
        });
        while (true) {
            Thread.sleep(TimeUnit.SECONDS.toMillis(50));
        }
    }

}