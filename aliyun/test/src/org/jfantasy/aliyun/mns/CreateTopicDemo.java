package org.jfantasy.aliyun.mns;


import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.TopicMeta;

public class CreateTopicDemo {

    public static void main(String[] args) {
        CloudAccount account = new CloudAccount("YourAccessId", "YourAccessKey", "MNSEndpoint");
        MNSClient client = account.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        String topicName = "TestTopic";
        TopicMeta meta = new TopicMeta();
        meta.setTopicName(topicName);
        try {
            CloudTopic topic = client.createTopic(meta);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("create topic error, " + e.getMessage());
        }
        client.close();
    }

}
