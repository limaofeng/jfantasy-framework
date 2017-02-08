package org.jfantasy.aliyun.mns;


import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.QueueMeta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateQueueDemo {

    private static final Log logger = LogFactory.getLog(CreateQueueDemo.class);

    public static void main(String[] args) {
        CloudAccount account = new CloudAccount("YourAccessId", "YourAccessKey", "MNSEndpoint");
        MNSClient client = account.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        String queueName = "TestQueue";
        QueueMeta meta = new QueueMeta(); //生成本地QueueMeta属性，有关队列属性详细介绍见https://help.aliyun.com/document_detail/27476.html
        meta.setQueueName(queueName);  // 设置队列名
        meta.setPollingWaitSeconds(15);
        meta.setMaxMessageSize(2048L);
        try {
            CloudQueue queue = client.createQueue(meta);
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
            }
        } catch (Exception e) {
            System.out.println("Unknown exception happened!");
            e.printStackTrace();
        }
        client.close();  // 程序退出时，需主动调用client的close方法进行资源释放
    }

}
