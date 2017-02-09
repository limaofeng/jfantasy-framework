package org.jfantasy.aliyun.api;

public class Message {

    private String id;
    private String body;
    private String tag;

    public Message(String body) {
        this.body = body;
    }

    public Message(String body, String tag) {
        this.body = body;
        this.tag = tag;
    }

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
