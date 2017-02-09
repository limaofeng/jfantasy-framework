package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.TopicMeta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.aliyun.api.Consumer;
import org.jfantasy.aliyun.api.MessageListener;
import org.jfantasy.framework.util.common.StringUtil;

import java.util.concurrent.TimeUnit;

public class ConsumerBean implements Consumer {

    private static final Log LOGGER = LogFactory.getLog(ConsumerBean.class);

    private MessageListener listener;
    private MNSClient client;
    private CloudQueue queue;
    private CloudTopic topic;
    private String topicName;
    private String queueName;
    private String subscriptionName;
    private String filterTag;

    public ConsumerBean(MNSClient client, String queueName) {
        this.client = client;
        this.queueName = queueName;
    }

    public ConsumerBean(MNSClient client, String topicName, String queueName, String subscriptionName) {
        this.client = client;
        this.topicName = topicName;
        this.queueName = queueName;
        this.subscriptionName = subscriptionName;
    }

    public ConsumerBean(MNSClient client, String topicName, String queueName, String subscriptionName, String filterTag) {
        this.client = client;
        this.topicName = topicName;
        this.queueName = queueName;
        this.subscriptionName = subscriptionName;
        this.filterTag = filterTag;
    }

    @Override
    public void start() {
        // step 1 : 创建队列
        this.queue = client.getQueueRef(this.queueName);
        if(this.queue == null) {
            QueueMeta queueMeta = new QueueMeta();
            queueMeta.setQueueName(this.queueName);
            queue = client.createQueue(queueMeta);
        }
        if (StringUtil.isNotBlank(this.topicName)) {
            // step 2 : 创建主题
            this.topic = client.getTopicRef(this.topicName);
            if(this.topic == null) {
                TopicMeta topicMeta = new TopicMeta();
                topicMeta.setTopicName(this.topicName);
                this.topic = client.createTopic(topicMeta);
            }
            // step 3 : 创建订阅
            SubscriptionMeta subMeta = topic.getSubscriptionAttr(this.subscriptionName);
            if(subMeta == null) {
                subMeta = new SubscriptionMeta();
                subMeta.setSubscriptionName(this.subscriptionName);
                subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.SIMPLIFIED);
                subMeta.setEndpoint(topic.generateQueueEndpoint(this.queueName));
                if (StringUtil.isNotBlank(this.filterTag)) {
                    subMeta.setFilterTag(this.filterTag);
                }
                topic.subscribe(subMeta);
            }
        }
        // step 4 : 从订阅的队列中获取消息
        new Thread(() -> {
            do {
                try {
                    Message popMsg = queue.popMessage(30);
                    if (popMsg == null) {
                        continue;
                    }
                    while (listener == null) {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                    }
                    if (listener.consume(new org.jfantasy.aliyun.api.Message(popMsg))) {
                        queue.deleteMessage(popMsg.getReceiptHandle());
                    }
                } catch (ClientException ce) {
                    LOGGER.error("Something wrong with the network connection between client and MNS service."
                            + "Please check your network and DNS availablity.", ce);
                } catch (ServiceException se) {
                    LOGGER.error("MNS exception requestId:" + se.getRequestId(), se);
                    if (se.getErrorCode() != null) {
                        if ("QueueNotExist".equals(se.getErrorCode())) {
                            LOGGER.error("Queue is not exist.Please create before use");
                        } else if ("TimeExpired".equals(se.getErrorCode())) {
                            LOGGER.error("The request is time expired. Please check your local machine timeclock");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Unknown exception happened!", e);
                }
            } while (client.isOpen());
        }).start();
    }


    @Override
    public void shutdown() {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Override
    public void on(MessageListener listener) {
        this.listener = listener;
    }

}
