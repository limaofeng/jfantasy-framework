package org.jfantasy.aliyun.api;

public class Message {

    private String id;
    private String body;

    public Message(com.aliyun.mns.model.Message message) {
        this.id = message.getMessageId();
        this.body = message.getMessageBody();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
