package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Base64TopicMessage;
import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.model.TopicMeta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.aliyun.api.Message;
import org.jfantasy.aliyun.api.Mode;
import org.jfantasy.aliyun.api.Producer;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 生产者
 */
public class ProducerBean implements Producer {

    private static final Log LOGGER = LogFactory.getLog(ProducerBean.class);

    private MNSClient client;
    private CloudQueue queue;
    private CloudTopic topic;
    private Mode mode;
    private String name;
    private boolean enable;

    public ProducerBean(MNSClient client, Mode mode, String name) {
        this.client = client;
        this.mode = mode;
        this.name = name;
        this.enable = Boolean.valueOf(StringUtil.defaultValue(System.getenv("aliyun.mns.enable"),"true"));
    }

    @Override
    public void start() {
        if (this.mode == Mode.queue) {
            this.queue = client.getQueueRef(this.name);
            if (this.queue == null || !this.queue.isQueueExist()) {
                QueueMeta queueMeta = new QueueMeta();
                queueMeta.setQueueName(this.name);
                this.queue = client.createQueue(queueMeta);
            }
        } else {
            this.topic = client.getTopicRef(this.name);
            if (this.topic == null) {
                TopicMeta topicMeta = new TopicMeta();
                topicMeta.setTopicName(this.name);
                this.topic = client.createTopic(topicMeta);
            }
        }
    }

    @Override
    public void shutdown() {
        this.client.close();
    }

    @Override
    public String send(String body) {
        if(!this.enable){
            LOGGER.error("消息功能已被禁用,请通过 aliyun.mns.enable=true 启用该功能");
            return null;
        }
        if (this.queue != null) {
            return queue.putMessage(new com.aliyun.mns.model.Message(body)).getMessageId();
        } else {
            return send(new Message(body)).getId();
        }
    }

    @Override
    public Message send(Message message) {
        if(!this.enable){
            LOGGER.error("消息功能已被禁用,请通过 aliyun.mns.enable=true 启用该功能");
            return null;
        }
        if (this.queue != null) {
            message.setId(send(message.getBody()));
            return message;
        } else {
            TopicMessage topicMessage = new Base64TopicMessage();
            topicMessage.setMessageBody(message.getBody());
            if (StringUtil.isNotBlank(message.getTag())) {
                topicMessage.setMessageTag(message.getTag());
            }
            message.setId(topic.publishMessage(topicMessage).getMessageId());
            return message;
        }
    }

}
