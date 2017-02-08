package org.jfantasy.aliyun.api;


public interface Producer {

    /**
     * 启动服务
     */
    void start();

    /**
     * 关闭服务
     */
    void shutdown();

    /**
     * 同步发送消息，只要不抛异常就表示成功
     *
     * @param message
     * @return 发送结果，消息Id
     */
    String send(final String message);

}
