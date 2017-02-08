package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.aliyun.api.Consumer;
import org.jfantasy.aliyun.api.MessageListener;

import java.util.concurrent.TimeUnit;

public class ConsumerBean implements Consumer {

    private static final Log LOGGER = LogFactory.getLog(ConsumerBean.class);

    private MessageListener listener;
    private MNSClient client;
    private String queueName;

    public ConsumerBean(MNSClient client, String queueName) {
        this.client = client;
        this.queueName = queueName;
    }

    @Override
    public void start() {
        CloudQueue queue = client.getQueueRef(this.queueName);
        do {
            try {
                Message popMsg = queue.popMessage();
                if (popMsg == null) {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
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
                LOGGER.error("Unknown exception happened!",e);
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
