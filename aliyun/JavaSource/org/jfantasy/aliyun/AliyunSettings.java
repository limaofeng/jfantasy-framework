package org.jfantasy.aliyun;

public class AliyunSettings {
    /**
     * 访问密钥
     */
    private String accessKey;
    private String secretKey;
    /**
     * ONS
     */
    private String topicId;
    private String producerId;
    private String consumerId;
    /**
     * MNS
     */
    private String accountEndpoint;
    private String queueName;

    public AliyunSettings() {//NOSONAR
    }

    public AliyunSettings(AliyunSettings aliyunSettings) {
        this.accessKey = aliyunSettings.getAccessKey();
        this.secretKey = aliyunSettings.getSecretKey();
        this.topicId = aliyunSettings.getTopicId();
        this.consumerId = aliyunSettings.getConsumerId();
        this.producerId = aliyunSettings.getProducerId();
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getAccountEndpoint() {
        return accountEndpoint;
    }

    public void setAccountEndpoint(String accountEndpoint) {
        this.accountEndpoint = accountEndpoint;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
