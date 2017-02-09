package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.*;

public class TopicSample {

    public static void main(String[] args) {
        CloudAccount account = new CloudAccount("44IzFLqkj8Pw2YOi", "XexMYOXZGBrPgQTUq4HtKuxxl9zMx1", "http://1744260525350210.mns.cn-hangzhou.aliyuncs.com");
        MNSClient client = account.getMNSClient();
        // step 1 : 创建队列
        QueueMeta queueMeta = new QueueMeta();
        queueMeta.setQueueName("TestSubForQueue");
        CloudQueue queue = client.createQueue(queueMeta);
        // step 2 : 创建主题
        TopicMeta topicMeta = new TopicMeta();
        topicMeta.setTopicName("TestTopic");
        CloudTopic topic = client.createTopic(topicMeta);
        // step 3 : 创建订阅
        SubscriptionMeta subMeta = new SubscriptionMeta();
        subMeta.setSubscriptionName("TestForQueueSub");
        subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.SIMPLIFIED);
        subMeta.setEndpoint(topic.generateQueueEndpoint("TestSubForQueue"));
        subMeta.setFilterTag("filterTag");
        topic.subscribe(subMeta);
        // step 4 : 发布消息
        TopicMessage msg = new Base64TopicMessage();
        msg.setMessageBody("hello world");
        msg.setMessageTag("filterTag");
        msg = topic.publishMessage(msg);
        // step 5 : 从订阅的队列中获取消息
        Message msgReceive = queue.popMessage(30);
        System.out.println("ReceiveMessage From TestSubForQueue:");
        System.out.println(msgReceive.getMessageBody());
        System.exit(0);
    }

}
