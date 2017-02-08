package org.jfantasy.aliyun.api;

public interface MessageListener {

    /**
     * 消费消息接口，由应用来实现<br>
     *
     * @param message
     *         消息
     * @return 消费结果
     */
    boolean consume(final Message message);

}
