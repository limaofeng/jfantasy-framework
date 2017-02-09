package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import org.jfantasy.aliyun.api.Mode;
import org.jfantasy.aliyun.api.Producer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProducerBeanTest {

    private Producer producer;

    @Before
    public void setUp() throws Exception {
        CloudAccount account = new CloudAccount("44IzFLqkj8Pw2YOi", "XexMYOXZGBrPgQTUq4HtKuxxl9zMx1", "http://1744260525350210.mns.cn-hangzhou.aliyuncs.com");
        MNSClient client = account.getMNSClient();

        producer = new ProducerBean(client, Mode.topic,"notification");
        producer.start();
    }

    @After
    public void tearDown() throws Exception {
        producer.shutdown();
    }

    @Test
    public void send() throws Exception {
        producer.send("你好啊");
    }

}