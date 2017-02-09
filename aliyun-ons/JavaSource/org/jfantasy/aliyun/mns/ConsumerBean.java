package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.SubscriptionMeta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.aliyun.api.Consumer;
import org.jfantasy.aliyun.api.MessageListener;
import org.jfantasy.framework.util.common.StringUtil;

public class ConsumerBean implements Consumer {

    private static final Log LOGGER = LogFactory.getLog(ConsumerBean.class);

    private MessageListener listener;
    private MNSClient client;
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
        if (StringUtil.isNotBlank(this.topicName)) {
            CloudTopic topic = client.getTopicRef(this.topicName);
            SubscriptionMeta subMeta = new SubscriptionMeta();
            subMeta.setSubscriptionName(this.subscriptionName);
            subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.SIMPLIFIED);
            subMeta.setEndpoint(topic.generateQueueEndpoint(this.queueName));
            if (StringUtil.isNotBlank(this.filterTag)) {
                subMeta.setFilterTag(this.filterTag);
            }
            topic.subscribe(subMeta);
        }

        CloudQueue queue = client.getQueueRef(this.queueName);
        do {
            try {
                Message popMsg = queue.popMessage(30);
                if (popMsg == null) {
                    continue;
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
