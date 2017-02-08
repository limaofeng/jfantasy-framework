package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.RawTopicMessage;
import org.jfantasy.aliyun.api.Mode;
import org.jfantasy.aliyun.api.Producer;

/**
 * 生产者
 */
public class ProducerBean implements Producer {

    private MNSClient client;
    private CloudQueue queue;
    private CloudTopic topic;
    private Mode mode;
    private String name;

    public ProducerBean(MNSClient client, Mode mode, String name) {
        this.client = client;
        this.mode = mode;
        this.name = name;
    }

    @Override
    public void start() {
        if (this.mode == Mode.queue) {
            this.queue = client.getQueueRef(this.name);
        } else {
            this.topic = client.getTopicRef(this.name);
        }
    }

    @Override
    public void shutdown() {
        this.client.close();
    }

    @Override
    public String send(String body) {
        if (this.queue != null) {
            return queue.putMessage(new Message(body)).getMessageId();
        } else {
            RawTopicMessage message = new RawTopicMessage();
            message.setMessageBody(body);
            return topic.publishMessage(message).getMessageId();
        }
    }
}
