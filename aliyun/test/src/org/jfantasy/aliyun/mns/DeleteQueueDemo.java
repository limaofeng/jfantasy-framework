package org.jfantasy.aliyun.mns;


import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeleteQueueDemo {

    private static final Log logger = LogFactory.getLog(DeleteQueueDemo.class);

    public static void main(String[] args) {
        CloudAccount account = new CloudAccount("YourAccessId", "YourAccessKey", "MNSEndpoint");
        MNSClient client = account.getMNSClient(); // 在程序中，CloudAccount以及MNSClient单例实现即可，多线程安全
        try {
            CloudQueue queue = client.getQueueRef("TestQueue");
            queue.delete();
        } catch (ClientException ce) {
            System.out.println("Something wrong with the network connection between client and MNS service."
                    + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        } catch (ServiceException se) {
            se.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown exception happened!");
            e.printStackTrace();
        }
        client.close();
    }
}