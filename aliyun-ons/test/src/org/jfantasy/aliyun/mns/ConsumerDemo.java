package org.jfantasy.aliyun.mns;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ConsumerDemo {

    private static final Log logger = LogFactory.getLog(ConsumerDemo.class);

    public static void main(String[] args) {
        CloudAccount account = new CloudAccount("YourAccessId", "YourAccessKey", "MNSEndpoint");
        MNSClient client = account.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        try {
            CloudQueue queue = client.getQueueRef("TestQueue");
            Message popMsg = queue.popMessage();
            if (popMsg != null) {
                System.out.println("message handle: " + popMsg.getReceiptHandle());
                System.out.println("message body: " + popMsg.getMessageBodyAsString());
                System.out.println("message id: " + popMsg.getMessageId());
                System.out.println("message dequeue count:" + popMsg.getDequeueCount());
                //删除已经取出消费的消息
                queue.deleteMessage(popMsg.getReceiptHandle());
                System.out.println("delete message successfully.\n");
            } else {
                System.out.println("message not exist in TestQueue.\n");
            }
        } catch (ClientException ce) {
            System.out.println("Something wrong with the network connection between client and MNS service."
                    + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        } catch (ServiceException se) {
            se.printStackTrace();
            logger.error("MNS exception requestId:" + se.getRequestId(), se);
            if (se.getErrorCode() != null) {
                if (se.getErrorCode().equals("QueueNotExist")) {
                    System.out.println("Queue is not exist.Please create before use");
                } else if (se.getErrorCode().equals("TimeExpired")) {
                    System.out.println("The request is time expired. Please check your local machine timeclock");
                }
            /*
            you can get more MNS service error code from following link:
            https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html
            */
            }
        } catch (Exception e) {
            System.out.println("Unknown exception happened!");
            e.printStackTrace();
        }
        client.close();
    }
}
