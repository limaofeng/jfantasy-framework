package org.jfantasy.aliyun.api;

/**
 * 消费者
 */
public interface Consumer {

    /**
     * 启动服务
     */
    void start();

    /**
     * 关闭服务
     */
    void shutdown();

    /**
     * 订阅消息
     */
    void on(MessageListener listener);

}
